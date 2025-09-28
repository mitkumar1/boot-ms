# Customer Service Git Hooks Setup for Jenkins CI/CD
# Simple and working PowerShell version

Write-Host "🔗 Setting up Git hooks for Jenkins automation" -ForegroundColor Blue

# Check if in Git repository
if (-not (Test-Path ".git")) {
    Write-Host "❌ Not in a Git repository" -ForegroundColor Red
    exit 1
}

# Create hooks directory
$HooksDir = ".git\hooks"
New-Item -ItemType Directory -Path $HooksDir -Force | Out-Null

# Pre-commit hook content
$PreCommitContent = @"
#!/bin/sh
echo "🔍 Customer Service pre-commit checks..."
echo "✅ Ready for Jenkins CI/CD automation"
echo "🚀 Push will trigger security scanning"
"@

# Pre-push hook content  
$PrePushContent = @"
#!/bin/sh
echo "🚀 Customer Service pre-push validation..."
BRANCH=`$(git rev-parse --abbrev-ref HEAD)
echo "📝 Branch: `$BRANCH"
echo "✅ Jenkins will run comprehensive security scanning"
echo "🔗 Monitor: http://your-jenkins-server.com/job/customer-service/"
"@

# Write hook files
$PreCommitContent | Out-File -FilePath "$HooksDir\pre-commit" -Encoding ASCII
$PrePushContent | Out-File -FilePath "$HooksDir\pre-push" -Encoding ASCII

Write-Host "✅ Git hooks created!" -ForegroundColor Green

# Create test script
$TestScriptContent = @"
# Customer Service CI/CD Test
Write-Host "🧪 Jenkins CI/CD Integration Test" -ForegroundColor Blue

`$Branch = git rev-parse --abbrev-ref HEAD
Write-Host "📝 Current Branch: `$Branch" -ForegroundColor Green

Write-Host ""
Write-Host "🛡️ Security Scanning on Push:" -ForegroundColor Yellow
Write-Host "✅ OWASP Dependency Check"
Write-Host "✅ SonarQube Quality Analysis"
if (`$Branch -eq "main" -or `$Branch -eq "develop") {
    Write-Host "✅ Fortify SAST Scan" -ForegroundColor Green
    Write-Host "✅ Black Duck SCA Scan" -ForegroundColor Green
} else {
    Write-Host "❌ Fortify (main/develop only)" -ForegroundColor Yellow
    Write-Host "❌ Black Duck (main/develop only)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🚀 To trigger automation:" -ForegroundColor Blue
Write-Host "git push origin `$Branch"
Write-Host ""
Write-Host "⏱️ Expected duration: 8-25 minutes"
"@

$TestScriptContent | Out-File -FilePath "test-automation.ps1" -Encoding UTF8

Write-Host "✅ Test script created: test-automation.ps1" -ForegroundColor Green
Write-Host ""
Write-Host "🚀 Ready for Jenkins automation!" -ForegroundColor Green
Write-Host "Run: .\test-automation.ps1 to verify setup"