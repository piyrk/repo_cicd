# Kubernetes Deployment (Local Without Docker Hub)

This guide shows how to deploy the Car Rental System on a local Kubernetes cluster (Docker Desktop, Minikube, or Kind) using locally built Docker images instead of pushing to Docker Hub.

## 1. Build Local Images
From the project root:
```
# Backend
docker build -t cicd_project-backend:latest ./car-rental-system-backend-master
# Frontend
docker build -t cicd_project-frontend:latest ./car-rental-system-frontend-master
```

## 2A. Docker Desktop Kubernetes (Recommended)
1. Enable Kubernetes in Docker Desktop Settings > Kubernetes.
2. Apply manifests:
```
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secret-db.yaml -f k8s/config-backend.yaml
kubectl apply -f k8s/mysql-pvc.yaml -f k8s/uploads-pvc.yaml
kubectl apply -f k8s/mysql-deployment.yaml -f k8s/backend-deployment.yaml -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/ingress.yaml
```
3. Install/Enable an ingress controller (Docker Desktop ships one by default).
4. Add host entry (PowerShell as Administrator):
```
Add-Content -Path C:\Windows\System32\drivers\etc\hosts "$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}') carrental.local"
```
5. Access: http://carrental.local/ (frontend), http://carrental.local/api/ (backend API).

## 2B. Minikube
```
minikube start
minikube kubectl -- apply -f k8s/namespace.yaml
minikube kubectl -- apply -f k8s/secret-db.yaml -f k8s/config-backend.yaml
minikube kubectl -- apply -f k8s/mysql-pvc.yaml -f k8s/uploads-pvc.yaml
# Build images inside Minikube daemon
minikube image build -t cicd_project-backend:latest ./car-rental-system-backend-master
minikube image build -t cicd_project-frontend:latest ./car-rental-system-frontend-master
minikube kubectl -- apply -f k8s/mysql-deployment.yaml -f k8s/backend-deployment.yaml -f k8s/frontend-deployment.yaml
minikube addons enable ingress
minikube kubectl -- apply -f k8s/ingress.yaml
```
Add host entry:
```
Add-Content -Path C:\Windows\System32\drivers\etc\hosts "$(minikube ip) carrental.local"
```

## 2C. Kind
```
kind create cluster
# Build locally then load into Kind
docker build -t cicd_project-backend:latest ./car-rental-system-backend-master
docker build -t cicd_project-frontend:latest ./car-rental-system-frontend-master
kind load docker-image cicd_project-backend:latest
kind load docker-image cicd_project-frontend:latest
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secret-db.yaml -f k8s/config-backend.yaml
kubectl apply -f k8s/mysql-pvc.yaml -f k8s/uploads-pvc.yaml
kubectl apply -f k8s/mysql-deployment.yaml -f k8s/backend-deployment.yaml -f k8s/frontend-deployment.yaml
# Install nginx ingress controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.10.1/deploy/static/provider/kind/deploy.yaml
kubectl apply -f k8s/ingress.yaml
```
Port-forward test if DNS not set:
```
kubectl -n carrental port-forward svc/frontend 3000:80
kubectl -n carrental port-forward svc/backend 8080:8080
```

## 3. Verify
```
kubectl -n carrental get pods
kubectl -n carrental get svc
kubectl -n carrental describe ingress carrental-ingress
kubectl -n carrental logs deployment/backend
```

## 4. Probes & Actuator
Currently backend liveness/readiness use TCP probes. To use HTTP health checks:
1. Add the dependency to `pom.xml`:
```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
2. Replace TCP probes in `k8s/backend-deployment.yaml` with HTTP GET on `/actuator/health`.

## 5. Next Improvements
- Use a dedicated DB user (add secret, adjust config).
- Add resource requests/limits for each container.
- Use StatefulSet for MySQL in production.
- Enable TLS for ingress.
- Externalize secrets using sealed-secrets or Vault.

## 6. Cleanup
```
kubectl delete namespace carrental
minikube stop # or kind delete cluster
```
