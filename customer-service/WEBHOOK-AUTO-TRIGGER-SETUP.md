# 🔗 Git Webhook Auto-Trigger Setup Guide

This guide shows you how to configure **automatic Jenkins builds** triggered by Git pushes with comprehensive **security scanning** and **SonarQube quality gates**.

## 🎯 What You'll Get

✅ **Automatic builds** on every Git push  
✅ **Comprehensive security scanning** (Fortify, Black Duck, OWASP)  
✅ **SonarQube quality gates** with automatic pass/fail  
✅ **Slack/Email notifications** for build results  
✅ **Branch-specific deployment** automation  

## 🔧 Step 1: Jenkins Webhook Configuration

### 1.1 Configure Jenkins Job
1. Open your Jenkins job: **Customer Service Pipeline**
2. Go to **Configure** → **Build Triggers**
3. Check these options:
   ```
   ☑️ GitHub hook trigger for GITScm polling
   ☑️ Poll SCM (Schedule: H/2 * * * *)
   ☑️ Build periodically (Schedule: H 2 * * *) [for nightly scans]
   ```

### 1.2 Add GitHub Integration Plugin
```bash
# Install via Jenkins Plugin Manager:
- GitHub Integration Plugin
- GitHub Branch Source Plugin  
- GitHub API Plugin
- Generic Webhook Trigger Plugin
```

## 🔗 Step 2: GitHub Repository Webhook Setup

### 2.1 GitHub Webhook Configuration
1. Go to your GitHub repository
2. **Settings** → **Webhooks** → **Add webhook**
3. Configure webhook:
   ```
   Payload URL: http://your-jenkins-server.com/github-webhook/
   Content type: application/json
   Secret: [your-webhook-secret]
   
   Trigger events:
   ☑️ Push events
   ☑️ Pull request events  
   ☑️ Branch or tag creation
   ☑️ Branch or tag deletion
   ```

### 2.2 Jenkins GitHub Configuration
1. **Manage Jenkins** → **Configure System**
2. **GitHub** section:
   ```
   GitHub Server: https://api.github.com
   Credentials: [GitHub Personal Access Token]
   Manage hooks: ☑️
   Override Hook URL: http://your-jenkins-server.com/github-webhook/
   ```

## 🛡️ Step 3: Security Scanning Automation

### 3.1 Scanning Matrix by Trigger Type

| Trigger Type | OWASP | SonarQube | Fortify | Black Duck | Deploy |
|--------------|-------|-----------|---------|------------|--------|
| **Push to feature/** | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Push to develop** | ✅ | ✅ | ✅ | ✅ | ✅ Dev |
| **Push to main** | ✅ | ✅ | ✅ | ✅ | Manual |
| **Nightly scan** | ✅ | ✅ | ✅ | ✅ | ❌ |
| **Pull Request** | ✅ | ✅ | ❌ | ❌ | ❌ |

### 3.2 Quality Gate Configuration

```yaml
# SonarQube Quality Gate Rules
sonarqube_rules:
  coverage: "> 80%"
  duplicated_lines: "< 3%"
  maintainability_rating: "A"
  reliability_rating: "A" 
  security_rating: "A"
  vulnerabilities: "= 0"
  security_hotspots: "= 0"

# Security Scanning Thresholds  
security_thresholds:
  fortify_critical: 0
  fortify_high: 5
  blackduck_policy_violations: 0
  owasp_cvss: 8.0
```

## 📊 Step 4: Notification Setup

### 4.1 Slack Integration
1. Create Slack App and get webhook URL
2. Add Jenkins credentials:
   ```
   Kind: Secret text
   ID: slack-webhook-url
   Secret: https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK
   ```

### 4.2 Email Notifications
1. **Manage Jenkins** → **Configure System** → **E-mail Notification**
2. Configure SMTP:
   ```
   SMTP Server: smtp.company.com
   Default Recipients: dev-team@company.com, security-team@company.com
   Reply-To Address: jenkins@company.com
   ```

## 🚀 Step 5: Test the Complete Workflow

### 5.1 Test Git Push Trigger
```bash
# Make a test change
echo "# Test webhook trigger" >> README.md
git add README.md
git commit -m "Test: Jenkins webhook auto-trigger"
git push origin develop

# Expected result:
# ✅ Jenkins build starts automatically
# ✅ All security scans run
# ✅ SonarQube quality gate evaluated
# ✅ Notifications sent to Slack/Email
```

### 5.2 Test Pull Request Trigger
```bash
# Create feature branch
git checkout -b feature/test-webhook
echo "# PR test" >> test-file.md
git add test-file.md
git commit -m "feat: test PR webhook trigger"
git push origin feature/test-webhook

# Create PR on GitHub
# Expected result:
# ✅ Jenkins PR build starts
# ✅ Basic security scanning runs
# ✅ SonarQube analysis on PR
# ✅ Build status posted to PR
```

## 📈 Step 6: Monitoring & Dashboard

### 6.1 Jenkins Blue Ocean View
- Install **Blue Ocean** plugin for modern pipeline view
- Access: `http://your-jenkins-server.com/blue`
- Benefits: Visual pipeline, branch overview, PR integration

### 6.2 Build Status Badges
Add to your README.md:
```markdown
![Build Status](http://your-jenkins-server.com/buildStatus/icon?job=customer-service)
![Quality Gate](https://sonarqube.company.com/api/project_badges/measure?project=customer-service-virtual-threads&metric=alert_status)
![Security Rating](https://sonarqube.company.com/api/project_badges/measure?project=customer-service-virtual-threads&metric=security_rating)
```

## 🔧 Step 7: Troubleshooting Common Issues

### 7.1 Webhook Not Triggering
```bash
# Check Jenkins logs
tail -f /var/log/jenkins/jenkins.log | grep -i webhook

# Test webhook manually
curl -X POST http://your-jenkins-server.com/github-webhook/ \
  -H "Content-Type: application/json" \
  -d '{"ref":"refs/heads/main","commits":[{"id":"test"}]}'

# Verify GitHub webhook delivery
# GitHub → Settings → Webhooks → Recent Deliveries
```

### 7.2 Security Scans Failing
```bash
# Check tool availability
which sourceanalyzer  # Fortify
which detect.sh       # Black Duck
mvn --version         # Maven OWASP plugin

# Verify credentials
# Jenkins → Credentials → System → Global credentials
```

### 7.3 SonarQube Quality Gate Issues
```bash
# Check SonarQube connection
curl -u admin:admin http://sonarqube-server:9000/api/system/status

# Verify quality gate configuration
# SonarQube → Quality Gates → customer-service-virtual-threads
```

## ⚡ Quick Verification Checklist

After setup, verify these work:

- [ ] Push to `main` → Full build + security scans + quality gate
- [ ] Push to `develop` → Full build + auto-deploy to dev
- [ ] Push to `feature/*` → Basic build + quality checks
- [ ] Create PR → PR build + status check on GitHub
- [ ] Nightly at 2 AM → Comprehensive security scan
- [ ] Slack notifications for build status
- [ ] Email alerts for security vulnerabilities
- [ ] SonarQube quality gate pass/fail logic
- [ ] Blue Ocean pipeline visualization

## 📊 Expected Build Times

| Trigger Type | Duration | Scans Included |
|--------------|----------|----------------|
| **Feature branch** | 8-12 min | OWASP, SonarQube, Unit Tests |
| **Develop branch** | 15-20 min | All scans + Deploy to Dev |
| **Main branch** | 18-25 min | All scans + Manual approval |
| **Nightly scan** | 25-35 min | Deep security analysis |

## 🎉 Success Indicators

When everything is working correctly, you'll see:

1. **GitHub**: ✅ Green check marks on commits
2. **Jenkins**: 🔵 Blue Ocean shows successful pipeline
3. **Slack**: 📢 Build notifications in team channel
4. **SonarQube**: 📊 Quality gate status updated
5. **Email**: 📧 Security alerts (only when issues found)

---

## 🚨 Security Alert Configuration

For immediate security notifications:

```yaml
# Critical security alerts (immediate notification)
critical_alerts:
  - fortify_critical_vulnerabilities > 0
  - blackduck_policy_violations > 0  
  - owasp_cvss_score >= 9.0
  - sonarqube_security_rating = E

# High priority alerts (within 1 hour)
high_alerts:
  - fortify_high_vulnerabilities > 5
  - blackduck_high_risk > 3
  - owasp_cvss_score >= 8.0
  - sonarqube_security_hotspots > 0
```

**Setup complete!** 🎉 Your Customer Service application now has **fully automated** CI/CD with comprehensive security scanning triggered by every Git push.

---
*Auto-generated from Jenkins pipeline configuration - $(date)*