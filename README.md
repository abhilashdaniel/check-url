# check-url
an instrumented service that ingests the given url response metrics to Prometheus/Grafana EXPOSITION

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Tear Down](#tear-down)
* [Dashboard images](#dashboard-images)


## General info
This project is simple Lorem ipsum dolor generator.
	
## Technologies
Java, Maven, Docker, Minikube/k8s, Prometheus, Grafana. 

A `dockerfile` is made available to build the project. Alternatively the image is also made available in Dockerhub: `kodampuli/check-url:v1`. The following is recommended if you want to compile the project locally. The development & testing was done on Ubuntu.

* openjdk-11
* maven
	
## Setup
Clone the project & build a docker image. If you're using Minikube as your k8s setup, reuse the Docker daemon from Minikube with `eval $(minikube docker-env)`.

```
$ minikube -p check-url start
$ eval $(minikube -p check-url docker-env)
$ cd {Dir}/check-url
$ docker build -t kodampuli/check-url:v1 {Dir}/check-url

$ kubectl create -f {Dir}/check-url/k8s/
```

Get the minikube url endpoints
```
$ minikube -p check-url service --url check-url
* url-1
* url-2
$ minikube -p check-url service --url prometheus
* url-3
$ minikube -p check-url service --url grafana
* url-4
```

External urls can be checked for its stats by a GET
```
$ curl -v {url-2}/https://httpstat.us/200
$ curl -v {url-2/https://httpstat.us/503
```

Exposition HTTPServer
```
$ curl -v {url-1}/metrics
```

Prometheus - Open `{url-3}` on a browser

Grafana - Open `{url-4}` on a browser


## Tear down 

```
$ kubectl delete -f {Dir}/check-url/k8s/
$ docker rmi {imageid}
$ minikube -p check-url stop
$ minikube -p check-url delete
```

## Dashboard images
