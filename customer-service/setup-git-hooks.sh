#!/bin/bash
# Git Hooks Setup Script for Customer Service CI/CD
# This script sets up local Git hooks to validate code before pushing to trigger Jenkins

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
GIT_HOOKS_DIR="$PROJECT_DIR/.git/hooks"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔗 Setting up Git hooks for automated Jenkins CI/CD${NC}"
echo "======================================"

# Function to log messages
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# Check if we're in a Git repository
if [ ! -d ".git" ]; then
    log_error "This directory is not a Git repository"
    exit 1
fi

# Create hooks directory if it doesn't exist
mkdir -p "$GIT_HOOKS_DIR"

# Pre-commit hook - Code quality checks before commit
log_info "Creating pre-commit hook..."
cat > "$GIT_HOOKS_DIR/pre-commit" << 'EOF'
#!/bin/bash
# Pre-commit hook for Customer Service
# Runs basic code quality checks before allowing commit

set -e

echo "🔍 Running pre-commit checks for Customer Service..."

# Check if Maven is available
if ! command -v mvn >/dev/null 2>&1; then
    echo "❌ Maven not found. Please install Maven."
    exit 1
fi

# Check if Java 21 is available
if ! java -version 2>&1 | grep -q "21"; then
    echo "⚠️ Warning: Java 21 not detected. Virtual Threads may not work correctly."
fi

# Run basic compilation check
echo "📦 Checking compilation..."
if ! mvn compile -q -DskipTests=true; then
    echo "❌ Compilation failed. Fix compilation errors before committing."
    exit 1
fi

# Run unit tests
echo "🧪 Running unit tests..."
if ! mvn test -q; then
    echo "❌ Unit tests failed. Fix failing tests before committing."
    exit 1
fi

# Check for common security issues in code
echo "🔒 Basic security check..."
if grep -r "password.*=" src/main/java/ 2>/dev/null | grep -v "// TODO\|// FIXME"; then
    echo "⚠️ Warning: Potential hardcoded passwords found. Please review."
fi

# Check for System.out.println (should use logger)
if grep -r "System\.out\.println\|System\.err\.println" src/main/java/ 2>/dev/null; then
    echo "⚠️ Warning: System.out.println found. Consider using proper logging."
fi

# Check for TODO/FIXME comments in main code
todo_count=$(grep -r "TODO\|FIXME" src/main/java/ 2>/dev/null | wc -l || echo "0")
if [ "$todo_count" -gt 0 ]; then
    echo "📝 Found $todo_count TODO/FIXME comments in main code"
fi

echo "✅ Pre-commit checks passed!"
echo "🚀 Jenkins will run comprehensive security scanning after push"
EOF

# Pre-push hook - Final checks before triggering Jenkins
log_info "Creating pre-push hook..."
cat > "$GIT_HOOKS_DIR/pre-push" << 'EOF'
#!/bin/bash
# Pre-push hook for Customer Service
# Final validation before triggering Jenkins CI/CD pipeline

set -e

echo "🚀 Running pre-push validation for Customer Service..."

# Get the current branch
BRANCH=$(git rev-parse --abbrev-ref HEAD)
echo "📝 Current branch: $BRANCH"

# Get commit information
COMMIT_MSG=$(git log -1 --pretty=%B)
AUTHOR=$(git log -1 --pretty=%an)

echo "👤 Author: $AUTHOR"
echo "💬 Commit: $COMMIT_MSG"

# Branch naming validation
case $BRANCH in
    main|develop)
        echo "🎯 Pushing to $BRANCH - Jenkins will run full security scanning"
        ;;
    feature/*)
        echo "🌟 Feature branch - Jenkins will run basic quality checks"
        ;;
    hotfix/*)
        echo "🚨 Hotfix branch - Jenkins will run expedited security checks"
        ;;
    release/*)
        echo "📦 Release branch - Jenkins will run comprehensive validation"
        ;;
    *)
        echo "⚠️ Non-standard branch name: $BRANCH"
        echo "Consider using: feature/*, hotfix/*, release/* naming convention"
        ;;
esac

# Commit message validation
if [ ${#COMMIT_MSG} -lt 10 ]; then
    echo "❌ Commit message too short. Please provide a descriptive commit message."
    exit 1
fi

# Check for conventional commit format (optional warning)
if ! echo "$COMMIT_MSG" | grep -qE "^(feat|fix|docs|style|refactor|test|chore|security)(\(.+\))?: .+"; then
    echo "💡 Consider using conventional commit format: feat|fix|docs|style|refactor|test|chore|security: description"
fi

# Final build verification
echo "🔨 Final build verification..."
if ! mvn clean compile -q -DskipTests=true; then
    echo "❌ Final build failed. Aborting push."
    exit 1
fi

# Notify about Jenkins automation
echo ""
echo "🔗 Jenkins Automation Summary:"
echo "================================"
case $BRANCH in
    main)
        echo "• Full security scanning (Fortify + Black Duck + OWASP)"
        echo "• SonarQube quality gate (must pass)"
        echo "• Manual approval required for production deployment"
        echo "• Slack/Email notifications sent"
        ;;
    develop)
        echo "• Full security scanning (Fortify + Black Duck + OWASP)"
        echo "• SonarQube quality gate (must pass)"
        echo "• Automatic deployment to development environment"
        echo "• Slack/Email notifications sent"
        ;;
    feature/*)
        echo "• Basic security scanning (OWASP)"
        echo "• SonarQube analysis (warnings only)"
        echo "• No automatic deployment"
        echo "• Slack notifications sent"
        ;;
esac
echo ""

echo "✅ Pre-push validation completed!"
echo "🚀 Push will trigger Jenkins CI/CD pipeline automatically"
echo "📊 Monitor build progress: http://your-jenkins-server.com/job/customer-service/"
EOF

# Post-commit hook - Information about triggered build
log_info "Creating post-commit hook..."
cat > "$GIT_HOOKS_DIR/post-commit" << 'EOF'
#!/bin/bash
# Post-commit hook for Customer Service
# Provides information about the commit and upcoming Jenkins build

COMMIT_HASH=$(git rev-parse --short HEAD)
BRANCH=$(git rev-parse --abbrev-ref HEAD)
COMMIT_MSG=$(git log -1 --pretty=%B)

echo ""
echo "✅ Commit successful!"
echo "🔗 Commit: $COMMIT_HASH on $BRANCH"
echo "💬 Message: $COMMIT_MSG"
echo ""
echo "🚀 Next steps:"
echo "1. Push to remote: git push origin $BRANCH"
echo "2. Jenkins will automatically trigger CI/CD pipeline"
echo "3. Monitor build: http://your-jenkins-server.com/job/customer-service/"
echo "4. Check Slack for build notifications"
echo ""
EOF

# Make hooks executable
chmod +x "$GIT_HOOKS_DIR/pre-commit"
chmod +x "$GIT_HOOKS_DIR/pre-push"
chmod +x "$GIT_HOOKS_DIR/post-commit"

log_success "Git hooks installed successfully!"

# Create a webhook test script
log_info "Creating webhook test script..."
cat > "$PROJECT_DIR/test-webhook.sh" << 'EOF'
#!/bin/bash
# Test script to verify Jenkins webhook integration

set -e

echo "🧪 Testing Jenkins webhook integration..."

# Test webhook endpoint
JENKINS_URL="http://your-jenkins-server.com"  # Update with your Jenkins URL
WEBHOOK_URL="$JENKINS_URL/github-webhook/"

echo "🔗 Testing webhook endpoint: $WEBHOOK_URL"

# Create test payload
TEST_PAYLOAD='{
  "ref": "refs/heads/main",
  "commits": [
    {
      "id": "test-webhook-trigger",
      "message": "Test: webhook integration",
      "author": {
        "name": "Test User",
        "email": "test@company.com"
      }
    }
  ],
  "repository": {
    "name": "customer-service",
    "url": "https://github.com/your-org/customer-service"
  }
}'

# Test webhook (uncomment when ready)
# curl -X POST "$WEBHOOK_URL" \
#   -H "Content-Type: application/json" \
#   -H "X-GitHub-Event: push" \
#   -d "$TEST_PAYLOAD"

echo "📝 To test webhook:"
echo "1. Update JENKINS_URL in this script"
echo "2. Uncomment the curl command"
echo "3. Run: ./test-webhook.sh"
echo ""
echo "🔍 Monitor Jenkins logs:"
echo "tail -f /var/log/jenkins/jenkins.log | grep -i webhook"
echo ""
echo "✅ Webhook test script created!"
EOF

chmod +x "$PROJECT_DIR/test-webhook.sh"

# Create Jenkins pipeline status check script
log_info "Creating pipeline status check script..."
cat > "$PROJECT_DIR/check-build-status.sh" << 'EOF'
#!/bin/bash
# Script to check Jenkins build status for current branch

set -e

BRANCH=$(git rev-parse --abbrev-ref HEAD)
COMMIT=$(git rev-parse --short HEAD)
JENKINS_URL="http://your-jenkins-server.com"  # Update with your Jenkins URL
JOB_NAME="customer-service"

echo "🔍 Checking build status for Customer Service..."
echo "📝 Branch: $BRANCH"
echo "🔗 Commit: $COMMIT"
echo ""

# Function to get build status (requires Jenkins CLI or API)
check_build_status() {
    echo "📊 Jenkins Build Status:"
    echo "========================"
    echo "🔗 Jenkins URL: $JENKINS_URL/job/$JOB_NAME/"
    echo "📱 Blue Ocean: $JENKINS_URL/blue/organizations/jenkins/$JOB_NAME/activity"
    echo ""
    
    # Note: Requires Jenkins API token for automated checking
    echo "💡 To check build status programmatically:"
    echo "1. Generate Jenkins API token"
    echo "2. Use Jenkins REST API: $JENKINS_URL/job/$JOB_NAME/api/json"
    echo "3. Or install Jenkins CLI"
}

check_build_status

echo "🔔 Notification Channels:"
echo "• Slack: #deployments channel"
echo "• Email: team@company.com"
echo ""
echo "🛡️ Security Scanning:"
echo "• OWASP Dependency Check: Always enabled"
echo "• Fortify SAST: main/develop branches"
echo "• Black Duck SCA: main/develop branches"  
echo "• SonarQube: All branches with PRs"
echo ""
echo "✅ Build status check complete!"
EOF

chmod +x "$PROJECT_DIR/check-build-status.sh"

# Summary
echo ""
log_success "Git hooks and automation scripts setup complete!"
echo ""
echo "📁 Files created:"
echo "  ✅ .git/hooks/pre-commit (code quality checks)"
echo "  ✅ .git/hooks/pre-push (final validation)"
echo "  ✅ .git/hooks/post-commit (information display)"
echo "  ✅ test-webhook.sh (webhook testing)"
echo "  ✅ check-build-status.sh (build status checker)"
echo ""
echo "🚀 Workflow Summary:"
echo "1. git commit → pre-commit hook validates code"
echo "2. git push → pre-push hook final checks"
echo "3. Jenkins receives webhook → automated CI/CD starts"
echo "4. Security scanning + quality gates executed"
echo "5. Notifications sent to Slack/Email"
echo "6. Auto-deployment (if configured for branch)"
echo ""
echo "📖 Next steps:"
echo "1. Update Jenkins URL in test-webhook.sh and check-build-status.sh"
echo "2. Configure GitHub webhook: WEBHOOK-AUTO-TRIGGER-SETUP.md"
echo "3. Test with: git commit -m 'test: webhook trigger' && git push"
echo ""
log_success "Ready for automated CI/CD! 🎉"