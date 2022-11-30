# Kind - SpringBoot + istio virtual service

## 說明

本篇主要是為了簡單介紹如何將一個基本spring boot的程式搬入k8s + istio環境中，必要的一些前置作業可見下列連結

[Kind in MacOS](https://blog.elliot.tw/?p=615)
[Kind – Istio on Mac](https://blog.elliot.tw/?p=622)

開始前先準備一個測試用的專案 - [Spring Boot - Single](https://github.com/ElliotChen/springkind/tree/main/single)
專案簡單的連個mysql，加個rest controller，一般的專案也就是這樣....。

Build Tool使用了gradle，準備了3個profile - `local`、`docker`、`k8s`
`local`為了本機測試用
`docker`則是在上k8s前驗證build與config的方式是否正常
`k8s`則是重點，相關的設定皆採用了k8s的模式，
包含

1. applicaton config yaml放入了config map，
2. db的連結使用了service，
3. db的密碼用了secret，
4. 對外的連結不用簡單的ingress，而用了service mesh的基本版本－istio的virtual service。

目前在業界的實用上，同一個k8s node會分多個namespace，用以在不同的環境如dev，staging，prod等；所以會有使用同一個image，僅在不同環境下用不同config map的情形，這部份會留到一下篇，用[`kustomize`](https://kustomize.io/)來達成。

接下來透過下列的步驟操作，順便說明要注意的事項

## Build Image

Spring Boot 的gradle plugin自帶build image 的指令，不要問為什麼沒有`Dockfile`了

### k8s

```sh
gradle clean bootBuildImage -x test -PactiveProfile=k8s
```

在gradle裡可以看到

```kotlin
tasks.withType<BootJar> {
	println("current profile is [$activeProfile]")
	if (activeProfile == "local") {
		exclude("application-docker.yml")
		exclude("application-k8s.yml")
	} else {
		exclude("application-*.yml")
	}
}
```

可以發現`application-k8s.yml`是不會被包到image中，這是剛才提到的，在k8s環境中，不同Profile可以共用同一個image，這種模式的優點是節省了建構時間，而且僅需驗證一份程式，缺點就是，拿到image是無法單獨執行的，這時config的控管就變成異常重要。



## Local Registry

Image Bulid 完了，但僅local可見，要讓之後的kind能拉回來，要加上一個local 的 image registry。

我將範例[localregistry - docker-compose.yaml](https://github.com/ElliotChen/springkind/blob/main/docker/localregistry/docker-compose.yml)放在github上，簡單的用docker-compose啟動即可。

```sh
localregistry> docker-compose up -d
```

這樣kind就能用yaml中使用的container name: `kind-registry`找到image registry。



## Push Image

簡單來說，上tag，再push到local registry。

```
docker tag elliot/single/k8s:latest localhost:5000/elliot/single/k8s:latest
docker push localhost:5000/elliot/single/k8s:latest
```



## Kind - Create Cluster and Install istio

接下來的操作檔案都放在[github - k8s/single](https://github.com/ElliotChen/springkind/tree/main/k8s/single)中，詳細的說明可以參考前面的連結

很快的按順序跑一次，

```sh
kind create cluster --name mbp --config 01_kind_cluster.yaml
kubectl apply -f 02_metallb-native.yaml ## 這步驟執行要等一下再往後
kubectl apply -f 03_metallb-config.yaml
istioctl install -f 04_istio_install.yaml
kubectl label namespace default istio-injection=enabled --overwrite
```

這樣就可以用`istioctl verify-install`來檢查基礎環境是否正常



## Kind - Install Service

接下來就安裝mysql與redis，redis是我裝開心的....

```sh
kubectl apply -f 05_redis_pv.yaml
kubectl apply -f 06_mysql_pv.yaml
```

這裡看到yaml的內容

```
    spec:
      containers:
      - name: mysql
        image:  mysql:8
        env:
        - name: MYSQL_ROOT_PASSWORD
          value: abcd1234
```

可以看到mysql root的密碼，當然正常是不會這樣做的，這裡先提醒一下，不好的習慣不要有，但這裡不是重點，而且該如何加密是另一門學問，也不是簡短篇幅可以寫完的。

## Kind - Deploy Spring Boot Image 

### Install Secret

```sh
kubectl apply -f 07_mysql_secret.yaml
```

正常的工作環境，有機密性的資料都是以`secret`儲存的，所以我們也建一個secret用來記錄mysql的root password，透過`echo -n "abcd1234" | base64`產生，也就是簡單的轉為base64格式，雖然不符機密等級需求，但在這也算過吧。

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
type: Opaque
data:
  root_password: YWJjZDEyMzQ=
```

### Deploy service

```sh
kubectl apply -f 08_istio_service.yaml
```

接下來就再細一點說明`08_istio_service.yaml`裡的內容

#### Config Map

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: single-cm
  namespace: default
data:
  application-k8s.yml: |
    spring:
      datasource:
        username: root
        password: ${SECRETS_MYSQL_ROOT_PASSWORD}
        url: jdbc:mysql://mysql-svc:3306/test?serverTimezone=UTC
      jpa:
        hibernate:
          ddl-auto: create
        database-platform: org.hibernate.dialect.MySQL8Dialect
        show-sql: true
        properties:
          hibernate:
            format_sql: true
      data:
        redis:
          repositories:
            enabled: true
          host: redis-svc
          port: 6379
```

在這裡我們將`application-k8s.yml`做為純文字內容寫在了yaml裡成為了config map；當然，這也是不好的，之後我會用kustomize讀取檔案建立相關的deploy yaml。

這裡要注意的是

1. `password: ${SECRETS_MYSQL_ROOT_PASSWORD}`，代表引入environment 變數做為內容

2. `jdbc:mysql://mysql-svc:3306/test?serverTimezone=UTC`，此處的mysql host: `mysql-svc`，是用先前`06_mysql_pv.yaml`裡設定的service name

   ```yaml
   apiVersion: v1
   kind: Service
   metadata:
     name: mysql-svc
   ```

其餘就是一般的spring設定，只是將其複製貼入而已。



### Deployment

再來看`deployment`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: single-deploy
  namespace: default
spec:
  replicas: 1
  selector:
    matchLabels:
      app: single
  template:
    metadata:
      labels:
        app: single
    spec:
      containers:
        - name: single
          image: localhost:5000/elliot/single/k8s:latest
          ports:
            - containerPort: 8080
          volumeMounts:
            - name: config-volume
              mountPath: /workspace/config
          env:
            - name: SECRETS_MYSQL_ROOT_PASSWORD
              #value: abcd1234
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: root_password
      volumes:
        - name: config-volume
          configMap:
            name: single-cm
      restartPolicy: Always
```

這裡有幾個地方要注意

#### image

```
image: localhost:5000/elliot/single/k8s:latest
```

內容是自local registry中取得

#### config map

```yaml
    spec:
      containers:
        - name: single
        ....
          volumeMounts:
            - name: config-volume
              mountPath: /workspace/config
      volumes:
        - name: config-volume
          configMap:
            name: single-cm
```

這裡將config map，也就是方才提到的`application-k8s.yaml`掛在了`/workspace/config`目錄下，所以spring boot 程式起動時可以讀 得到。

#### environment

```yaml
          env:
            - name: SECRETS_MYSQL_ROOT_PASSWORD
              #value: abcd1234
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: root_password
```

這裡就是建立環境變數`SECRETS_MYSQL_ROOT_PASSWORD`的部份，內容則是讀取`mysql-secret.root_password`，也就是先前`07_mysql_secret.yaml`寫入的內容。再將其傳入`application-k8s.yaml`中

#### Virtual Service Rewrite

為了讓多個Service共用一個gateway進入點，避免建立多個LoadBalancer，可以用url prefix來分別各Service或者真的把prefix直接設定在Springboot的Request Mapping裡，這部份的取捨可以看整體架構的規劃，但個人比較推薦使用prefix + rewrite。

```yaml
...
  http:
  - match:
    - uri:
        prefix: "/single/"
    rewrite:
        uri: "/"
    route:
    - destination:
        host: single-svc
        port:
          number: 8080
```

## 驗證

用browser或curl連到`http://localhost/single/users/111`都能檢視結果

```
> curl http://localhost/single/users/111 -i

HTTP/1.1 200 OK
content-type: application/json
date: Thu, 27 Oct 2022 00:26:03 GMT
x-envoy-upstream-service-time: 30
server: istio-envoy
transfer-encoding: chunked

{"id":null,"name":null,"age":null}
```

## 結語

很多朋友在轉入K8s時遇到不少困難，但其實如果基礎環境已經建好，如image registry, mysql, redis, istio等都已完成佈置，剩餘的ap問題其實也還好，但難也難在這裡，以往來說，如果不花錢去租一個，連測試都不容易，即使租了一個，剛開始一定有很多使用上的測試會遇到困難，有錯也難找，找到了要重試也困難，所以能在本機有一個可以讓我們不擔心，可無限次犯錯的環境很重要。

先前使用minikube，能用，但不開心，遇到了kind後，真的解決了許多困擾，可以在很短的時間全部打掉重來，完全降低了系統轉移的驗證成本。

不過還是建議在從一般server轉到K8s的中間，可以先透過docker compose的方式去測試，畢竟這兩者的相似度極高，要考量的env與pv都可以在docker compose裡先想清楚，某方面來說，可以將local -> docker compose -> k8s做為一個設定變更的工作流水線，先前也想過透過kcompose來做這樣的工作，不過還是因為某些原因放棄了。

總之，無論是web系統或是後端api，只要概念正確，轉到K8s都不是難事。



### cmd

```sh

gradle clean bootBuildImage -x test -PactiveProfile=docker
gradle clean bootBuildImage -x test -PactiveProfile=k8s

docker tag elliot/single/k8s:latest localhost:5000/elliot/single/k8s:latest
docker push localhost:5000/elliot/single/k8s:latest

echo -n 'super-secret-password' | base64


kustomize build ./overlays/dev

kustomize build ./overlays/default | k apply -f -  
```


```
http://localhost/single/users/111
http://localhost/dev-single/users/111
```