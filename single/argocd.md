# Kind - Argocd



## 說明

[argo cd](https://argo-cd.readthedocs.io/en/stable/)提供了一個良好的UI 介面來進行更版，不過名稱訂為"CD"，也就代表"CI"不是主要功能...



## 安裝

### 準備

要在本機的kind 與 istio下安裝 Argocd，有些東西要先設定

#### 1. FQDN

由於argocd在login時會redirect到root path，所以要在istio下單單使用rewrite uri的方式就不太可能了

例如使用`http://localhost/argocd/`來做為進入點時會收到301轉到`http://localhost`，所以就無法再繼續下去，比較簡單的是另定gateway與virtualservice，所以用hostname做為gateway的分別是比較好的方式。

在本機端就用修改`/etc/host`，加上一個自定的FQDN

```
127.0.0.1 argocd.elliot.tw
```

#### 2. 決定修改insecure的方式

因為`argocd`預設是用https的方式連結的，但接在istio之後，自帶tls其實是不方式的，所以要將argocd改為http連結。

主要改法有幾種：

1. 改config map: 先下載[argocd-cmd-params-cm.yaml](https://raw.githubusercontent.com/argoproj/argo-cd/master/docs/operator-manual/argocd-cmd-params-cm.yaml)，將不要的部份移除，保留`server.insecure`並改為`true`，如下列
   ```yaml
   apiVersion: v1
   kind: ConfigMap
   metadata:
     name: argocd-cmd-params-cm
     namespace: argocd
     labels:
       app.kubernetes.io/name: argocd-cmd-params-cm
       app.kubernetes.io/part-of: argocd
   data:
     server.insecure: "true"
   ```

2. 修改deployment，這方式我倒是比較不建議使用，至少改configmap比較可以明視瞭解，改了deployment後不容易讓其他人看到變更的原因。

#### 3. 新建namespece並加入istio inject config

利用下列yaml先行建立argocd要用的namespace，並開啟`istio-injection: enabled`

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: argocd
  labels:
    istio-injection: enabled
```



### 正式安裝

1. Argocd manifests
   ```
   kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
   ```


2. update config map
   將剛才改完的configmap yaml apply
   
   ```
   kubectl apply -f argocd-cmd-params-cm.yaml
   ```
   
3. restart argocd-server
   ```
   kubectl rollout restart deployments -n argocd argocd-server
   ```

4. install istio gateway and virtual service
   ```
   apiVersion: networking.istio.io/v1alpha3
   kind: Gateway
   metadata:
     name: argocd-gateway
     namespace: argocd
   spec:
     selector:
       istio: ingressgateway # use istio default controller
     servers:
     - port:
         number: 80
         name: http
         protocol: HTTP
       hosts:
       - "argocd.elliot.tw"
   ---
   apiVersion: networking.istio.io/v1alpha3
   kind: VirtualService
   metadata:
     name: argocd-vs
     namespace: argocd
   spec:
     hosts:
     - "argocd.elliot.tw"
     gateways:
     - argocd-gateway
     http:
     - route:
       - destination:
           host: argocd-server
           port:
             number: 80
   ```

    這裡使用了剛才在`hosts`加入的fqdn，稍後的連結也用這個fqdn

5. 取得admin密碼

   ```
   kubectl get secrets/argocd-initial-admin-secret -n argocd --template={{.data.password}} | base64 -D
   ```

   每次安裝的argocd都會產生一個新密碼，找出`secret`後再decode即可
   
## 操作

透過`http://argocd.elliot.tw`連入，帳號預設是`admin`，密碼就看secret了

![Screen Shot 2022-11-03 at 15.32.12](https://picgo.ap-south-1.linodeobjects.com/20221103/40d31dff1316cbc6ccc8bb858c10650c.png)



進入後按下`NEW APP`，逐步加入project，這裡我仍是用先前的範例[Github - Springkind](https://github.com/ElliotChen/springkind.git)

Application Name隨意，可以分別是哪個環境即可，Project Name則是`default`

![Screen Shot 2022-11-02 at 14.09.31](https://picgo.ap-south-1.linodeobjects.com/20221103/40bb273f3fdff653c912ae637acea994.png)

輸入git repository的url，argocd在載入後會分析，如果有`kustomization.yaml`就會在path中列出，這裡是dev，所以選overlays/default

![Screen Shot 2022-11-02 at 14.09.44](https://picgo.ap-south-1.linodeobjects.com/20221103/c6406bd4e502d90efa661689a2542113.png)

之後的destination就選定要api server的位置與namespace，但這些在`kustomization.yaml`裡都訂好了。

![Screen Shot 2022-11-02 at 14.10.11](https://picgo.ap-south-1.linodeobjects.com/20221103/a79726dffea4069be51ee927a8a42a4a.png)

重覆兩次把dev與default都加入，有沒有按`SYNC`會有不同的顯示狀態，要完成一個Deploy就按`SYNC`即可。

![Screen Shot 2022-11-02 at 14.41.57](https://picgo.ap-south-1.linodeobjects.com/20221103/4f69fc13f612a6272f0473b884e51462.png)