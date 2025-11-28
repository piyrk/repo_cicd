#requires -RunAsAdministrator
param(
    [string]$ClusterName = "kind",
    [string]$Namespace = "carrental",
    [string]$IngressVersion = "controller-v1.10.1"
)

function Exec($cmd) {
  Write-Host "==> $cmd" -ForegroundColor Cyan
  Invoke-Expression $cmd
  if ($LASTEXITCODE -ne 0) { throw "Command failed: $cmd" }
}

# 1) Ensure kind cluster
try { $clusters = kind get clusters 2>$null } catch { $clusters = @() }
if (-not ($clusters -contains $ClusterName)) { Exec "kind create cluster --name $ClusterName" }

# 2) Build images
Exec "docker build -t cicd_project-backend:latest .\car-rental-system-backend-master"
Exec "docker build -t cicd_project-frontend:latest .\car-rental-system-frontend-master"

# 3) Load images into kind
Exec "kind load docker-image cicd_project-backend:latest --name $ClusterName"
Exec "kind load docker-image cicd_project-frontend:latest --name $ClusterName"

# 4) Apply namespace, secrets/config, PVCs, deployments
Exec "kubectl apply -f k8s/namespace.yaml"
Exec "kubectl -n $Namespace apply -f k8s/secret-db.yaml -f k8s/config-backend.yaml"
Exec "kubectl -n $Namespace apply -f k8s/mysql-pvc.yaml -f k8s/uploads-pvc.yaml"
Exec "kubectl -n $Namespace apply -f k8s/mysql-deployment.yaml -f k8s/backend-deployment.yaml -f k8s/frontend-deployment.yaml"

# 5) Install nginx ingress controller for kind
Exec "kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/$IngressVersion/deploy/static/provider/kind/deploy.yaml"

# 6) Apply ingress
Exec "kubectl -n $Namespace apply -f k8s/ingress.yaml"

# 7) Map hosts entry to localhost (Kind ingress exposes hostPorts 80/443)
$hostsPath = "$env:WINDIR\System32\drivers\etc\hosts"
$entry = "127.0.0.1 carrental.local"
$exists = (Get-Content $hostsPath) -match "carrental\\.local"
if (-not $exists) { Add-Content -Path $hostsPath -Value $entry }

Write-Host "\nDeployment complete. Open http://carrental.local/" -ForegroundColor Green
