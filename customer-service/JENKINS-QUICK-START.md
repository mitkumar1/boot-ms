# 🚀 Quick Jenkins CI/CD Setup for Virtual Thread Spring Boot App

## 📋 What You've Got

I've created a complete Jenkins CI/CD pipeline for your Spring Boot application with Virtual Threads:

### 📁 New Files Created:
- `Jenkinsfile` - Complete CI/CD pipeline
- `JENKINS-SETUP.md` - Detailed setup documentation  
- `dependency-check-suppressions.xml` - Security scan configuration
- `k8s/deployment-jenkins.yaml` - Kubernetes deployment for Jenkins
- `k8s/service-jenkins.yaml` - Kubernetes service for Jenkins
- `jenkins-deploy.sh` - Deployment script

## ⚡ Quick Setup (5 minutes)

### 1. **Install Jenkins Plugins**
Go to **Manage Jenkins** → **Manage Plugins** and install:
- Pipeline
- Docker Pipeline Plugin
- Maven Integration Plugin
- JUnit Plugin

### 2. **Configure Java 21**
Go to **Manage Jenkins** → **Global Tool Configuration**:
- **JDK**: Name: `JDK-21`, Auto-install JDK 21
- **Maven**: Name: `Maven-3.9`, Auto-install Maven 3.9

### 3. **Create Pipeline Job**
1. **New Item** → **Pipeline** → Name: `customer-service-cicd`
2. **Pipeline Definition**: Pipeline script from SCM
3. **SCM**: Git → Repository URL: `[your-git-repo-url]`
4. **Script Path**: `Jenkinsfile`

### 4. **Add Docker Registry Credentials**
**Manage Jenkins** → **Manage Credentials**:
- **Kind**: Username with password
- **ID**: `docker-registry-credentials`
- **Username/Password**: Your Docker registry credentials

## 🎯 What the Pipeline Does

### 🔍 **Code Quality & Security**
- SonarQube analysis with Java 21 support
- OWASP dependency vulnerability scanning
- Code coverage reporting

### 🏗️ **Build & Test**
- Maven build with Java 21 and `--enable-preview`
- Unit tests with Virtual Thread configuration
- Integration tests with Docker container
- Test result publishing

### 🐳 **Docker**
- Builds optimized image with Virtual Thread JVM flags
- Security scanning with Trivy
- Multi-tag support (build number + latest)
- Registry push for main/develop branches

### 🚀 **Deployment**
- Development environment (develop branch)
- Production environment (main branch with approval)
- Kubernetes deployment with Virtual Thread optimization
- Health checks and verification

### 📢 **Notifications**
- Slack notifications for success/failure
- Email alerts for production failures
- Build status reporting

## 🧵 Virtual Thread Features

Your pipeline specifically optimized for Virtual Threads:

### JVM Configuration
```bash
JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseZGC --enable-preview"
VIRTUAL_THREAD_OPTS="-Djdk.virtualThreadScheduler.parallelism=10 -Djdk.virtualThreadScheduler.maxPoolSize=256"
```

### Verification Steps
- Tests virtual thread endpoints during integration testing
- Verifies `Virtual: true` in application logs
- Performance testing with virtual thread-specific endpoints

## 🔧 Environment Variables

Configure these in Jenkins **Global Properties**:

```bash
# Docker Configuration
DOCKER_REGISTRY=your-registry.com

# Kubernetes
DEV_NAMESPACE=customer-service-dev
PROD_NAMESPACE=customer-service-prod

# Notifications (Optional)
SLACK_CHANNEL=#deployments
EMAIL_RECIPIENTS=team@company.com
```

## 🎮 How to Use

### 🚀 **Trigger Build**
1. **Manual**: Go to job → **Build Now**
2. **Git Push**: Automatic trigger on push to main/develop
3. **Webhook**: Configure GitHub/GitLab webhook

### 📊 **Monitor Progress**
- **Blue Ocean**: Modern pipeline visualization
- **Console Output**: Detailed logs
- **Build History**: Track all builds

### 🎯 **Deploy to Production**
1. Pipeline runs automatically on main branch push
2. Waits for approval at "Deploy to Production" stage
3. Click **Proceed** to deploy
4. Monitors deployment health

## 🔍 Monitoring Your Virtual Threads

After deployment, check these endpoints:

```bash
# Application running at (adjust port)
http://your-app-url/

# Virtual thread info
http://your-app-url/api/v1/virtual-threads/info

# Health check (should show Virtual: true in logs)
http://your-app-url/actuator/health

# Scheduled tasks (running on virtual threads)
http://your-app-url/actuator/scheduledtasks
```

## 🐛 Troubleshooting

### **Build Fails - Java 21 Not Found**
```bash
# Fix: Configure JDK-21 in Global Tool Configuration
Manage Jenkins → Global Tool Configuration → JDK → Add JDK 21
```

### **Docker Permission Denied**
```bash
# Fix: Add jenkins user to docker group
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### **Virtual Threads Not Working**
```bash
# Check logs for:
Virtual: true  # Should appear in scheduled task logs
--enable-preview  # Should be in JVM args
```

### **Pipeline Fails at Kubernetes Deploy**
```bash
# Fix: Configure kubectl and cluster access
kubectl config current-context  # Should show your cluster
```

## ✅ Success Indicators

Your pipeline is working correctly when you see:

1. ✅ **Build**: Java 21 compilation with `--enable-preview`
2. ✅ **Tests**: All tests pass including virtual thread tests  
3. ✅ **Docker**: Image builds with virtual thread optimization
4. ✅ **Deploy**: Kubernetes deployment with proper health checks
5. ✅ **Verify**: Virtual thread endpoints respond correctly
6. ✅ **Logs**: `Virtual: true` appears in application logs

## 📚 Next Steps

1. **Customize** the Jenkinsfile for your specific needs
2. **Configure** SonarQube for code quality gates
3. **Set up** Slack/email notifications
4. **Add** more environments (staging, QA)
5. **Implement** blue-green deployments
6. **Monitor** virtual thread performance metrics

---

🎉 **Your Jenkins CI/CD pipeline is ready to deploy your Virtual Thread-enabled Spring Boot application!**

**Need help?** Check the detailed `JENKINS-SETUP.md` for complete configuration instructions.