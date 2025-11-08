# Script to kill the backend process running on port 8080

Write-Host "Finding process using port 8080..." -ForegroundColor Cyan

$process = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique

if ($process) {
    Write-Host "Process ID: $process" -ForegroundColor Yellow
    $procInfo = Get-Process -Id $process
    Write-Host "Process Name: $($procInfo.ProcessName)" -ForegroundColor Yellow
    Write-Host "Command: $($procInfo.Path)" -ForegroundColor Yellow
    
    Write-Host "`nKilling process..." -ForegroundColor Red
    Stop-Process -Id $process -Force
    Write-Host "Process killed successfully!" -ForegroundColor Green
} else {
    Write-Host "No process found on port 8080" -ForegroundColor Yellow
}

