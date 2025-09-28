# Customer Service CI/CD Test
Write-Host "ðŸ§ª Jenkins CI/CD Integration Test" -ForegroundColor Blue

$Branch = git rev-parse --abbrev-ref HEAD
Write-Host "ðŸ“ Current Branch: $Branch" -ForegroundColor Green

Write-Host ""
Write-Host "ðŸ›¡ï¸ Security Scanning on Push:" -ForegroundColor Yellow
Write-Host "âœ… OWASP Dependency Check"
Write-Host "âœ… SonarQube Quality Analysis"
if ($Branch -eq "main" -or $Branch -eq "develop") {
    Write-Host "âœ… Fortify SAST Scan" -ForegroundColor Green
    Write-Host "âœ… Black Duck SCA Scan" -ForegroundColor Green
} else {
    Write-Host "âŒ Fortify (main/develop only)" -ForegroundColor Yellow
    Write-Host "âŒ Black Duck (main/develop only)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "ðŸš€ To trigger automation:" -ForegroundColor Blue
Write-Host "git push origin $Branch"
Write-Host ""
Write-Host "â±ï¸ Expected duration: 8-25 minutes"
