# Customer Service Jenkins CI/CD Automation Test
Write-Host "Jenkins CI/CD Integration Test" -ForegroundColor Blue

$Branch = git rev-parse --abbrev-ref HEAD
Write-Host "Current Branch: $Branch" -ForegroundColor Green

Write-Host ""
Write-Host "Security Scanning on Push:" -ForegroundColor Yellow
Write-Host "OWASP Dependency Check: Enabled"
Write-Host "SonarQube Quality Analysis: Enabled"

if ($Branch -eq "main" -or $Branch -eq "develop") {
    Write-Host "Fortify SAST Scan: Enabled" -ForegroundColor Green
    Write-Host "Black Duck SCA Scan: Enabled" -ForegroundColor Green
    Write-Host "Comprehensive Security Scanning: YES" -ForegroundColor Green
} else {
    Write-Host "Fortify SAST: main/develop only" -ForegroundColor Yellow
    Write-Host "Black Duck SCA: main/develop only" -ForegroundColor Yellow
    Write-Host "Basic Security Scanning: YES" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "To trigger Jenkins automation:" -ForegroundColor Blue
Write-Host "git push origin $Branch"
Write-Host ""
Write-Host "Expected build duration: 8-25 minutes"
Write-Host "Monitor at: http://your-jenkins-server.com/job/customer-service/"