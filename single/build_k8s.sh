#/bin/bash

gradle clean bootBuildImage -x test -PactiveProfile=k8s

docker tag elliot/single/k8s:latest localhost:5000/elliot/single/k8s:latest
docker push localhost:5000/elliot/single/k8s:latest