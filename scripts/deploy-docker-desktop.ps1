#requires -RunAsAdministrator
param(
    [string]$Namespace = "carrental"
)

function Exec($cmd) {
  Write-Host "==> $cmd" -ForegroundColor Cyan
  Invoke-Expression $cmd
  if ($LASTEXITCODE -ne 0) { throw "Command failed: $cmd" }
}

# 1) Switch to Docker Desktop context
Write-Host "Switching to docker-desktop context..." -ForegroundColor Yellow
try {
    kubectl config use-context docker-desktop
} catch {
    Write-Error "Could not switch to 'docker-desktop' context. Ensure Docker Desktop Kubernetes is enabled."
    exit 1
}

# 2) Build images (Docker Desktop uses local daemon, so no load step needed)
Exec "docker build -t cicd_project-backend:latest .\car-rental-system-backend-master"
Exec "docker build -t cicd_project-frontend:latest .\car-rental-system-frontend-master"

# 3) Apply namespace, secrets/config, PVCs, deployments
Exec "kubectl apply -f k8s/namespace.yaml"
Exec "kubectl -n $Namespace apply -f k8s/secret-db.yaml -f k8s/config-backend.yaml"
Exec "kubectl -n $Namespace apply -f k8s/mysql-pvc.yaml -f k8s/uploads-pvc.yaml"
Exec "kubectl -n $Namespace apply -f k8s/mysql-deployment.yaml -f k8s/backend-deployment.yaml -f k8s/frontend-deployment.yaml"

# Force restart to pick up new images (since tags are 'latest')
Write-Host "Restarting deployments to pick up new images..." -ForegroundColor Yellow
Exec "kubectl -n $Namespace rollout restart deployment backend"
Exec "kubectl -n $Namespace rollout restart deployment frontend"

# 4) Apply ingress
# Note: Docker Desktop usually needs an ingress controller. If not present, we install NGINX.
Write-Host "Checking for Ingress Controller..." -ForegroundColor Yellow
$ingressPod = kubectl get pods -n ingress-nginx --no-headers 2>$null
if (-not $ingressPod) {
    Write-Host "Installing NGINX Ingress Controller for Docker Desktop..." -ForegroundColor Yellow
    Exec "kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.10.1/deploy/static/provider/cloud/deploy.yaml"
    Write-Host "Waiting for Ingress Controller to be ready..."
    kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=120s
}

Exec "kubectl -n $Namespace apply -f k8s/ingress.yaml"

# 5) Map hosts entry
$hostsPath = "$env:WINDIR\System32\drivers\etc\hosts"
$entry = "127.0.0.1 carrental.local"
$exists = (Get-Content $hostsPath) -match "carrental\\.local"
if (-not $exists) { 
    Write-Host "Adding entry to hosts file..." -ForegroundColor Yellow
    Add-Content -Path $hostsPath -Value $entry 
}

Write-Host "\nDeployment complete on Docker Desktop!" -ForegroundColor Green
Write-Host "Frontend: http://carrental.local/" -ForegroundColor Green
Write-Host "Backend API: http://carrental.local/api/" -ForegroundColor Green
