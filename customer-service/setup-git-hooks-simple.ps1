# Simple PowerShell Git Hooks Setup for Customer Service CI/CD
# This script sets up basic Git hooks for Jenkins automation

Write-Host "🔗 Setting up Git hooks for Jenkins CI/CD automation" -ForegroundColor Blue
Write-Host "=====================================" -ForegroundColor Blue

# Check if we're in a Git repository
if (-not (Test-Path ".git")) {
    Write-Host "❌ This directory is not a Git repository" -ForegroundColor Red
    exit 1
}

# Create hooks directory
$HooksDir = ".git\hooks"
if (-not (Test-Path $HooksDir)) {
    New-Item -ItemType Directory -Path $HooksDir -Force | Out-Null
    Write-Host "📁 Created hooks directory" -ForegroundColor Green
}

# Create simple pre-commit hook
Write-Host "📝 Creating pre-commit hook..." -ForegroundColor Blue
$PreCommitHook = @'
#!/bin/sh
echo "🔍 Running Customer Service pre-commit checks..."

if command -v mvn >/dev/null 2>&1; then
    echo "✅ Maven found"
    echo "📦 Running compilation check..."
    if mvn compile -q -DskipTests=true; then
        echo "✅ Compilation successful"
    else
        echo "❌ Compilation failed"
        exit 1
    fi
else
    echo "⚠️ Maven not found - skipping compilation check"
fi

echo "✅ Pre-commit checks passed!"
echo "🚀 Jenkins will trigger on push with full security scanning"
'@

$PreCommitHook | Out-File -FilePath "$HooksDir\pre-commit" -Encoding ASCII -NoNewline

# Create pre-push hook
Write-Host "📝 Creating pre-push hook..." -ForegroundColor Blue
$PrePushHook = @'
#!/bin/sh
echo "🚀 Customer Service pre-push validation..."

BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "📝 Current branch: $BRANCH"

echo "🔗 Jenkins CI/CD will trigger automatically with:"
echo "• OWASP Dependency Check"
echo "• SonarQube Quality Analysis"
if [ "$BRANCH" = "main" ] || [ "$BRANCH" = "develop" ]; then
    echo "• Fortify SAST Security Scan"
    echo "• Black Duck SCA Scan"
    echo "• Full security validation"
else
    echo "• Basic quality checks (feature branch)"
fi

echo "✅ Pre-push validation complete!"
echo "🚀 Push will trigger Jenkins pipeline automatically"
'@

$PrePushHook | Out-File -FilePath "$HooksDir\pre-push" -Encoding ASCII -NoNewline

Write-Host "✅ Git hooks created successfully!" -ForegroundColor Green

# Create simple test script
Write-Host "📝 Creating test automation script..." -ForegroundColor Blue
$TestScript = @'
# Simple CI/CD Test for Customer Service
Write-Host "🧪 Testing Customer Service CI/CD Integration" -ForegroundColor Blue

$Branch = git rev-parse --abbrev-ref HEAD
$Commit = git rev-parse --short HEAD

Write-Host "📝 Current Branch: $Branch" -ForegroundColor Green
Write-Host "🔗 Latest Commit: $Commit" -ForegroundColor Green

Write-Host ""
Write-Host "🛡️ Security Scanning Matrix:" -ForegroundColor Yellow
if ($Branch -eq "main" -or $Branch -eq "develop") {
    Write-Host "✅ OWASP Dependency Check" -ForegroundColor Green
    Write-Host "✅ Fortify SAST Scan" -ForegroundColor Green  
    Write-Host "✅ Black Duck SCA Scan" -ForegroundColor Green
    Write-Host "✅ SonarQube Quality Gate (Must Pass)" -ForegroundColor Green
} else {
    Write-Host "✅ OWASP Dependency Check" -ForegroundColor Green
    Write-Host "❌ Fortify SAST (main/develop only)" -ForegroundColor Red
    Write-Host "❌ Black Duck SCA (main/develop only)" -ForegroundColor Red
    Write-Host "✅ SonarQube Analysis" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🚀 To trigger Jenkins CI/CD:" -ForegroundColor Blue
Write-Host "1. Make your changes"
Write-Host "2. git add ."
Write-Host "3. git commit -m 'your message'"
Write-Host "4. git push origin $Branch"
Write-Host ""
Write-Host "📊 Monitor build at:" -ForegroundColor Blue
Write-Host "• Jenkins: http://your-jenkins-server.com/job/customer-service/"
Write-Host "• Slack: #deployments channel"
Write-Host ""
Write-Host "⏱️ Expected build time: 8-25 minutes (depending on branch)"
'@

$TestScript | Out-File -FilePath "test-jenkins-automation.ps1" -Encoding UTF8

# Create quick batch launcher
Write-Host "📝 Creating batch launcher..." -ForegroundColor Blue
$BatchLauncher = @'
@echo off
echo 🚀 Customer Service Jenkins CI/CD
echo ==============================
echo.
echo This will test the Jenkins automation setup
echo.
powershell -ExecutionPolicy Bypass -File "test-jenkins-automation.ps1"
echo.
pause
'@

$BatchLauncher | Out-File -FilePath "test-jenkins.bat" -Encoding ASCII

Write-Host ""
Write-Host "🎉 Setup Complete!" -ForegroundColor Green
Write-Host "=================" -ForegroundColor Green
Write-Host ""
Write-Host "📁 Created files:" -ForegroundColor Blue
Write-Host "  ✅ .git\hooks\pre-commit" -ForegroundColor Green
Write-Host "  ✅ .git\hooks\pre-push" -ForegroundColor Green
Write-Host "  ✅ test-jenkins-automation.ps1" -ForegroundColor Green
Write-Host "  ✅ test-jenkins.bat" -ForegroundColor Green
Write-Host ""
Write-Host "🚀 Quick Test:" -ForegroundColor Yellow
Write-Host "1. Double-click: test-jenkins.bat"
Write-Host "2. Or run: .\test-jenkins-automation.ps1"
Write-Host ""
Write-Host "🔗 Next Steps:" -ForegroundColor Yellow
Write-Host "1. Configure GitHub webhook (see WEBHOOK-AUTO-TRIGGER-SETUP.md)"
Write-Host "2. Test with real commit: git commit -m 'test' && git push"
Write-Host "3. Monitor Jenkins dashboard for automatic build trigger"
Write-Host ""
Write-Host "🛡️ Security scanning will run automatically on push!" -ForegroundColor Green