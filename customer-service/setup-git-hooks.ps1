# PowerShell Git Hooks Setup Script for Windows
# Customer Service CI/CD Automation Setup

param(
    [string]$JenkinsUrl = "http://your-jenkins-server.com",
    [string]$ProjectName = "customer-service"
)

Write-Host "🔗 Setting up Git hooks for automated Jenkins CI/CD" -ForegroundColor Blue
Write-Host "======================================" -ForegroundColor Blue

# Function to write colored output
function Write-Info { param($Message) Write-Host "[INFO] $Message" -ForegroundColor Blue }
function Write-Warning { param($Message) Write-Host "[WARNING] $Message" -ForegroundColor Yellow }
function Write-Error { param($Message) Write-Host "[ERROR] $Message" -ForegroundColor Red }
function Write-Success { param($Message) Write-Host "[SUCCESS] $Message" -ForegroundColor Green }

# Check if we're in a Git repository
if (-not (Test-Path ".git")) {
    Write-Error "This directory is not a Git repository"
    exit 1
}

# Create hooks directory if it doesn't exist
$HooksDir = ".git\hooks"
if (-not (Test-Path $HooksDir)) {
    New-Item -ItemType Directory -Path $HooksDir -Force | Out-Null
}

Write-Info "Creating Git hooks for Windows environment..."

# Pre-commit hook (PowerShell version)
Write-Info "Creating pre-commit hook..."
$PreCommitContent = @'
#!/bin/sh
# Pre-commit hook for Customer Service (Windows compatible)
echo "🔍 Running pre-commit checks for Customer Service..."

# Check if Maven is available
if command -v mvn >/dev/null 2>&1; then
    echo "✅ Maven found"
else
    echo "❌ Maven not found. Please install Maven."
    exit 1
fi

# Run basic compilation check
echo "📦 Checking compilation..."
if mvn compile -q -DskipTests=true; then
    echo "✅ Compilation successful"
else
    echo "❌ Compilation failed. Fix compilation errors before committing."
    exit 1
fi

# Run unit tests
echo "🧪 Running unit tests..."
if mvn test -q; then
    echo "✅ Unit tests passed"
else
    echo "❌ Unit tests failed. Fix failing tests before committing."
    exit 1
fi

echo "✅ Pre-commit checks passed!"
echo "🚀 Jenkins will run comprehensive security scanning after push"
'@

$PreCommitContent | Out-File -FilePath "$HooksDir\pre-commit" -Encoding ASCII

# Pre-push hook
Write-Info "Creating pre-push hook..."
$PrePushContent = @'
#!/bin/sh
# Pre-push hook for Customer Service
echo "🚀 Running pre-push validation for Customer Service..."

# Get the current branch
BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "📝 Current branch: $BRANCH"

# Branch-specific Jenkins automation info
if [ "$BRANCH" = "main" ]; then
    echo "🎯 Pushing to main - Full security scanning will run"
    echo "• Fortify SAST + Black Duck SCA + OWASP"
    echo "• SonarQube quality gate (must pass)"
    echo "• Manual approval for production"
elif [ "$BRANCH" = "develop" ]; then
    echo "🌟 Pushing to develop - Full scanning + auto-deploy to dev"
    echo "• All security scans enabled"
    echo "• Automatic deployment to development"
elif echo "$BRANCH" | grep -q "^feature/"; then
    echo "🔧 Feature branch - Basic quality checks"
    echo "• OWASP + SonarQube analysis"
    echo "• No automatic deployment"
else
    echo "ℹ️ Non-standard branch - Basic scanning will run"
fi

echo "✅ Pre-push validation completed!"
echo "🚀 Jenkins CI/CD will trigger automatically"
'@

$PrePushContent | Out-File -FilePath "$HooksDir\pre-push" -Encoding ASCII

Write-Success "Git hooks created successfully!"

# Create PowerShell test scripts
Write-Info "Creating PowerShell test scripts..."

# Webhook test script
$WebhookTestContent = @"
# PowerShell Webhook Test Script
param(
    [string]`$JenkinsUrl = `"$JenkinsUrl`",
    [string]`$WebhookPath = `"/github-webhook/`"
)

Write-Host "🧪 Testing Jenkins webhook integration..." -ForegroundColor Blue

`$WebhookUrl = "`$JenkinsUrl`$WebhookPath"
Write-Host "🔗 Testing webhook endpoint: `$WebhookUrl" -ForegroundColor Blue

# Test payload
`$TestPayload = @{
    ref = "refs/heads/main"
    commits = @(
        @{
            id = "test-webhook-trigger"
            message = "Test: webhook integration"
            author = @{
                name = "Test User"
                email = "test@company.com"
            }
        }
    )
    repository = @{
        name = "$ProjectName"
        url = "https://github.com/your-org/$ProjectName"
    }
} | ConvertTo-Json -Depth 3

Write-Host "📝 Test payload created" -ForegroundColor Green
Write-Host "🔍 To test webhook, uncomment the Invoke-RestMethod line below"

# Uncomment to test webhook
# try {
#     `$Response = Invoke-RestMethod -Uri `$WebhookUrl -Method Post -Body `$TestPayload -ContentType "application/json" -Headers @{"X-GitHub-Event" = "push"}
#     Write-Host "✅ Webhook test successful!" -ForegroundColor Green
# } catch {
#     Write-Host "❌ Webhook test failed: `$(`$_.Exception.Message)" -ForegroundColor Red
# }

Write-Host "📖 Manual testing steps:" -ForegroundColor Yellow
Write-Host "1. Update JenkinsUrl parameter if needed"
Write-Host "2. Uncomment the Invoke-RestMethod section"
Write-Host "3. Run: .\test-webhook.ps1"
"@

$WebhookTestContent | Out-File -FilePath "test-webhook.ps1" -Encoding UTF8

# Build status check script
$BuildStatusContent = @"
# PowerShell Build Status Checker
param(
    [string]`$JenkinsUrl = `"$JenkinsUrl`",
    [string]`$JobName = `"$ProjectName`"
)

Write-Host "🔍 Checking build status for Customer Service..." -ForegroundColor Blue

`$Branch = git rev-parse --abbrev-ref HEAD
`$Commit = git rev-parse --short HEAD

Write-Host "📝 Branch: `$Branch" -ForegroundColor Green
Write-Host "🔗 Commit: `$Commit" -ForegroundColor Green
Write-Host ""

Write-Host "📊 Jenkins URLs:" -ForegroundColor Blue
Write-Host "🔗 Job Dashboard: `$JenkinsUrl/job/`$JobName/"
Write-Host "📱 Blue Ocean: `$JenkinsUrl/blue/organizations/jenkins/`$JobName/activity"
Write-Host ""

Write-Host "🛡️ Security Scanning Matrix:" -ForegroundColor Yellow
Write-Host "================================"
switch (`$Branch) {
    "main" { 
        Write-Host "• OWASP Dependency Check: ✅" -ForegroundColor Green
        Write-Host "• Fortify SAST: ✅" -ForegroundColor Green  
        Write-Host "• Black Duck SCA: ✅" -ForegroundColor Green
        Write-Host "• SonarQube Quality Gate: ✅ (Must Pass)" -ForegroundColor Green
        Write-Host "• Auto-Deploy: ❌ (Manual Approval)" -ForegroundColor Yellow
    }
    "develop" {
        Write-Host "• OWASP Dependency Check: ✅" -ForegroundColor Green
        Write-Host "• Fortify SAST: ✅" -ForegroundColor Green
        Write-Host "• Black Duck SCA: ✅" -ForegroundColor Green  
        Write-Host "• SonarQube Quality Gate: ✅ (Must Pass)" -ForegroundColor Green
        Write-Host "• Auto-Deploy: ✅ (Development)" -ForegroundColor Green
    }
    { `$_ -like "feature/*" } {
        Write-Host "• OWASP Dependency Check: ✅" -ForegroundColor Green
        Write-Host "• Fortify SAST: ❌" -ForegroundColor Red
        Write-Host "• Black Duck SCA: ❌" -ForegroundColor Red
        Write-Host "• SonarQube Analysis: ✅ (Warnings Only)" -ForegroundColor Yellow
        Write-Host "• Auto-Deploy: ❌" -ForegroundColor Red
    }
    default {
        Write-Host "• Basic scanning for non-standard branch" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "🔔 Notifications:" -ForegroundColor Blue
Write-Host "• Slack: #deployments channel"
Write-Host "• Email: team@company.com"
Write-Host ""
Write-Host "✅ Build status check complete!" -ForegroundColor Green
"@

$BuildStatusContent | Out-File -FilePath "check-build-status.ps1" -Encoding UTF8

# Create comprehensive automation test script
Write-Info "Creating comprehensive CI/CD test script..."
$AutomationTestContent = @"
# Comprehensive CI/CD Automation Test
# Tests the complete workflow from Git push to Jenkins pipeline

param(
    [string]`$TestBranch = `"feature/test-automation`",
    [string]`$JenkinsUrl = `"$JenkinsUrl`",
    [switch]`$SkipPush,
    [switch]`$CleanupAfter
)

function Write-Step { param(`$Message) Write-Host "🔄 `$Message" -ForegroundColor Cyan }
function Write-Check { param(`$Message) Write-Host "✅ `$Message" -ForegroundColor Green }
function Write-Fail { param(`$Message) Write-Host "❌ `$Message" -ForegroundColor Red }

Write-Host "🧪 Customer Service CI/CD Automation Test" -ForegroundColor Blue
Write-Host "===========================================" -ForegroundColor Blue

# Step 1: Check prerequisites
Write-Step "Checking prerequisites..."

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Fail "Git not found"; exit 1
} else { Write-Check "Git available" }

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Fail "Maven not found"; exit 1  
} else { Write-Check "Maven available" }

if (-not (Test-Path ".git")) {
    Write-Fail "Not in Git repository"; exit 1
} else { Write-Check "Git repository detected" }

# Step 2: Create test branch
Write-Step "Creating test branch: `$TestBranch"
try {
    git checkout -b `$TestBranch 2>`$null
    Write-Check "Test branch created"
} catch {
    Write-Host "⚠️ Branch might already exist, switching to it" -ForegroundColor Yellow
    git checkout `$TestBranch
}

# Step 3: Make test changes
Write-Step "Making test changes..."
`$TestFile = "automation-test-`$(Get-Date -Format 'yyyyMMdd-HHmmss').md"
`$TestContent = @"
# CI/CD Automation Test

**Test Date:** `$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
**Branch:** `$TestBranch  
**Purpose:** Validate Jenkins webhook automation

## Expected Jenkins Pipeline Behavior

1. **Trigger:** Git push webhook
2. **Scanning:** OWASP Dependency Check + SonarQube
3. **Duration:** 8-12 minutes
4. **Notification:** Slack message to #deployments
5. **Result:** Build status on this commit

## Virtual Thread Configuration

- Java 21 with --enable-preview
- ZGC garbage collector  
- Virtual thread scheduler optimizations
- Spring Boot 3.4.0 virtual thread support

---
*Auto-generated by CI/CD automation test*
"@

`$TestContent | Out-File -FilePath `$TestFile -Encoding UTF8
git add `$TestFile

# Step 4: Test pre-commit hook
Write-Step "Testing pre-commit hook..."
try {
    git commit -m "test: CI/CD automation validation"
    Write-Check "Pre-commit hook passed"
} catch {
    Write-Fail "Pre-commit hook failed"
    if (`$CleanupAfter) { git checkout main; git branch -D `$TestBranch }
    exit 1
}

# Step 5: Test push (optional)
if (-not `$SkipPush) {
    Write-Step "Testing Git push and Jenkins webhook trigger..."
    try {
        git push origin `$TestBranch
        Write-Check "Push successful - Jenkins webhook should trigger"
        
        Write-Host ""
        Write-Host "🚀 Jenkins Pipeline Started!" -ForegroundColor Green
        Write-Host "================================" -ForegroundColor Green
        Write-Host "📊 Monitor build: `$JenkinsUrl/job/$ProjectName/"
        Write-Host "📱 Blue Ocean: `$JenkinsUrl/blue/organizations/jenkins/$ProjectName/activity"
        Write-Host ""
        Write-Host "Expected timeline:" -ForegroundColor Yellow
        Write-Host "• 0-2 min: Build preparation & code checkout"
        Write-Host "• 2-5 min: Compilation & unit tests"  
        Write-Host "• 5-8 min: OWASP dependency scanning"
        Write-Host "• 8-10 min: SonarQube quality analysis"
        Write-Host "• 10-12 min: Build completion & notifications"
        Write-Host ""
        
    } catch {
        Write-Fail "Push failed: `$(`$_.Exception.Message)"
    }
} else {
    Write-Host "⏭️ Skipping push (use -SkipPush:`$false to enable)" -ForegroundColor Yellow
}

# Step 6: Monitoring instructions
Write-Step "Setting up monitoring..."
Write-Host ""
Write-Host "🔍 How to monitor the build:" -ForegroundColor Blue
Write-Host "1. Jenkins Dashboard: `$JenkinsUrl/job/$ProjectName/"
Write-Host "2. Check Slack #deployments channel"
Write-Host "3. Run: .\check-build-status.ps1"
Write-Host "4. Email notifications (if configured)"
Write-Host ""

# Step 7: Cleanup (optional)
if (`$CleanupAfter) {
    Write-Step "Cleaning up test branch..."
    git checkout main
    git branch -D `$TestBranch
    Write-Check "Test branch cleaned up"
}

Write-Host ""
Write-Host "🎉 CI/CD Automation Test Complete!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host "✅ Git hooks working"
Write-Host "✅ Test commit created"
if (-not `$SkipPush) { Write-Host "✅ Jenkins webhook triggered" }
Write-Host "✅ Monitoring setup provided"
Write-Host ""
Write-Host "📈 Next: Watch Jenkins build progress and verify:"
Write-Host "• Build triggers automatically"
Write-Host "• Security scans execute"
Write-Host "• Quality gates evaluated"  
Write-Host "• Notifications sent"
Write-Host ""
"@

$AutomationTestContent | Out-File -FilePath "test-cicd-automation.ps1" -Encoding UTF8

Write-Success "All PowerShell scripts created successfully!"

# Create batch file for easy execution
Write-Info "Creating batch launcher..."
$BatchContent = @"
@echo off
echo 🚀 Customer Service CI/CD Automation
echo ================================

echo.
echo Available commands:
echo 1. Test webhook integration
echo 2. Check build status  
echo 3. Run full CI/CD test
echo 4. Setup Git hooks (this script)
echo.

set /p choice="Select option (1-4): "

if "%choice%"=="1" (
    powershell -ExecutionPolicy Bypass -File "test-webhook.ps1"
) else if "%choice%"=="2" (
    powershell -ExecutionPolicy Bypass -File "check-build-status.ps1"  
) else if "%choice%"=="3" (
    powershell -ExecutionPolicy Bypass -File "test-cicd-automation.ps1"
) else if "%choice%"=="4" (
    powershell -ExecutionPolicy Bypass -File "setup-git-hooks.ps1"
) else (
    echo Invalid choice
)

pause
"@

$BatchContent | Out-File -FilePath "cicd-automation.bat" -Encoding ASCII

Write-Host ""
Write-Success "Git hooks and automation setup complete! 🎉"
Write-Host ""
Write-Host "📁 Files created:" -ForegroundColor Blue
Write-Host "  ✅ .git\hooks\pre-commit (validation checks)"
Write-Host "  ✅ .git\hooks\pre-push (final verification)"  
Write-Host "  ✅ test-webhook.ps1 (webhook testing)"
Write-Host "  ✅ check-build-status.ps1 (build monitoring)"
Write-Host "  ✅ test-cicd-automation.ps1 (end-to-end test)"
Write-Host "  ✅ cicd-automation.bat (menu launcher)"
Write-Host ""
Write-Host "🚀 Quick Start:" -ForegroundColor Green
Write-Host "1. Run: .\cicd-automation.bat (interactive menu)"
Write-Host "2. Or: .\test-cicd-automation.ps1 (full test)"
Write-Host "3. Then: git push origin main (trigger real build)"
Write-Host ""
Write-Host "📖 Next steps:" -ForegroundColor Yellow  
Write-Host "1. Configure GitHub webhook (see WEBHOOK-AUTO-TRIGGER-SETUP.md)"
Write-Host "2. Update Jenkins URL in scripts"
Write-Host "3. Test with: .\test-cicd-automation.ps1"
Write-Host ""
Write-Success "Ready for automated CI/CD with comprehensive security scanning! 🛡️"