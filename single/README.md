## kind - springboot + istio virtualservice

### local

```sh
gradle clean bootJar -PactiveProfile=local
```

### docker

```sh
gradle clean bootBuildImage -PactiveProfile=docker
```


### k8s

```sh
gradle clean bootBuildImage -PactiveProfile=k8s
```