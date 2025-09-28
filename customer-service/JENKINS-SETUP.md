# 🚀 Jenkins CI/CD Setup Guide for Customer Service with Virtual Threads

This guide explains how to set up Jenkins CI/CD pipeline for your Spring Boot application with Java 21 Virtual Threads.

## 📋 Prerequisites

### Jenkins Requirements
- Jenkins 2.400+ with Pipeline plugin
- Java 21 JDK configured in Jenkins
- Maven 3.9+ configured in Jenkins
- Docker installed and accessible from Jenkins
- Kubernetes CLI (kubectl) if deploying to Kubernetes

### Required Jenkins Plugins
```bash
# Core Pipeline Plugins
Pipeline
Pipeline: Stage View
Blue Ocean (optional but recommended)

# Build & Test Plugins
Maven Integration Plugin
JUnit Plugin
Jacoco Plugin
HTML Publisher Plugin

# Code Quality Plugins
SonarQube Scanner Plugin
OWASP Dependency Check Plugin

# Security Scanning Plugins
Fortify Plugin
Black Duck Hub Plugin
Checkmarx Plugin (optional)

# Docker & Deployment Plugins
Docker Pipeline Plugin
Kubernetes Plugin
Kubernetes CLI Plugin

# Notification Plugins
Slack Notification Plugin
Email Extension Plugin

# Security Plugins
Credentials Plugin
Role-based Authorization Strategy
```

## 🔧 Jenkins Configuration

### 1. Global Tool Configuration

Go to **Manage Jenkins** → **Global Tool Configuration**:

#### Java (JDK 21)
```
Name: JDK-21
JAVA_HOME: /usr/lib/jvm/java-21-openjdk
Install automatically: ✓ (or provide path to JDK 21)
```

#### Maven
```
Name: Maven-3.9
MAVEN_HOME: /usr/share/maven
Install automatically: ✓ (Maven 3.9.x)
```

#### Docker
```
Name: Docker
Installation root: /usr/bin/docker
Install automatically: ✓
```

### 2. System Configuration

#### Environment Variables
Add these global environment variables in **Manage Jenkins** → **Configure System**:

```bash
# Virtual Thread Configuration
JAVA_OPTS=-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseZGC -XX:+UnlockExperimentalVMOptions --enable-preview
VIRTUAL_THREAD_OPTS=-Djdk.virtualThreadScheduler.parallelism=10 -Djdk.virtualThreadScheduler.maxPoolSize=256

# Docker Registry
DOCKER_REGISTRY=your-registry.com

# Notification Settings
SLACK_TEAM=your-team
DEFAULT_EMAIL=team@company.com
```

### 3. Credentials Setup

Add these credentials in **Manage Jenkins** → **Manage Credentials**:

#### Docker Registry Credentials
```
Kind: Username with password
ID: docker-registry-credentials
Username: [your-docker-username]
Password: [your-docker-password]
Description: Docker Registry Access
```

#### Kubernetes Credentials
```
Kind: Secret file
ID: kubeconfig
File: [your-kubeconfig-file]
Description: Kubernetes Cluster Access
```

#### Slack Token (Optional)
```
Kind: Secret text
ID: slack-token
Secret: [your-slack-bot-token]
Description: Slack Notifications
```

## �️ Security Tools Configuration

### Fortify Static Code Analyzer Setup
1. Install Fortify SCA on Jenkins agents:
   ```bash
   # Download and install Fortify SCA
   wget https://your-fortify-server/Fortify_SCA_XX.X.X_Linux_x64.run
   chmod +x Fortify_SCA_XX.X.X_Linux_x64.run
   ./Fortify_SCA_XX.X.X_Linux_x64.run
   ```

2. Configure Fortify in Jenkins:
   - Go to **Manage Jenkins** → **Global Tool Configuration**
   - Add **Fortify Assessment** tool:
     ```
     Name: Fortify-SCA
     Installation directory: /opt/fortify/SCA_XX.X.X
     ```

3. Add Fortify credentials:
   ```
   Kind: Username with password
   ID: fortify-credentials
   Username: [fortify-username]
   Password: [fortify-password]
   Description: Fortify SCA Access
   ```

### Black Duck Hub Configuration
1. Configure Black Duck in Jenkins:
   - Go to **Manage Jenkins** → **Configure System**
   - Find **Black Duck Hub** section:
     ```
     Hub URL: https://your-blackduck-server.com
     Username: [blackduck-username]
     Password: [blackduck-password]
     Hub Timeout: 120
     ```

2. Add Black Duck credentials:
   ```
   Kind: Secret text
   ID: blackduck-api-token
   Secret: [your-blackduck-api-token]
   Description: Black Duck API Token
   ```

   ```
   Kind: Secret text
   ID: blackduck-url
   Secret: https://your-blackduck-server.com
   Description: Black Duck Server URL
   ```

3. Configure Black Duck project settings:
   ```json
   {
     "projectName": "customer-service-virtual-threads",
     "versionName": "1.0.0",
     "codeLocationName": "customer-service-main",
     "scanMemory": 4096,
     "scanTimeout": 600
   }
   ```

## �📊 SonarQube Integration

### SonarQube Server Configuration
1. Go to **Manage Jenkins** → **Configure System**
2. Find **SonarQube servers** section
3. Add SonarQube server:
   ```
   Name: SonarQube
   Server URL: https://your-sonarqube-server.com
   Server authentication token: [your-sonarqube-token]
   ```

### Quality Gate Setup
Create a quality gate in SonarQube with Java 21 and Virtual Thread specific rules:
```json
{
  "coverage": "> 80%",
  "duplicated_lines_density": "< 3%",
  "maintainability_rating": "A",
  "reliability_rating": "A",
  "security_rating": "A",
  "java_version": "21"
}
```

## 🐳 Docker Configuration

### Docker Daemon Access
Ensure Jenkins has access to Docker daemon:

```bash
# Add jenkins user to docker group
sudo usermod -aG docker jenkins

# Restart Jenkins
sudo systemctl restart jenkins
```

### Docker Registry Setup
Configure Docker registry authentication:

```bash
# For Jenkins user
sudo -u jenkins docker login your-registry.com
```

## ☸️ Kubernetes Deployment Configuration

### Deployment Manifests

Create Kubernetes deployment files:

#### `k8s/deployment.yaml`
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: customer-service
  namespace: customer-service-prod
spec:
  replicas: 3
  selector:
    matchLabels:
      app: customer-service
  template:
    metadata:
      labels:
        app: customer-service
    spec:
      containers:
      - name: customer-service
        image: your-registry.com/customer-service:latest
        ports:
        - containerPort: 4040
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: JAVA_OPTS
          value: "-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseZGC -XX:+UnlockExperimentalVMOptions --enable-preview"
        - name: VIRTUAL_THREAD_OPTS
          value: "-Djdk.virtualThreadScheduler.parallelism=10 -Djdk.virtualThreadScheduler.maxPoolSize=256"
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 4040
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 4040
          initialDelaySeconds: 30
          periodSeconds: 5
```

#### `k8s/service.yaml`
```yaml
apiVersion: v1
kind: Service
metadata:
  name: customer-service-svc
  namespace: customer-service-prod
spec:
  selector:
    app: customer-service
  ports:
  - port: 80
    targetPort: 4040
    protocol: TCP
  type: ClusterIP
```

## 🔨 Pipeline Setup

### 1. Create New Pipeline Job

1. Go to Jenkins Dashboard
2. Click **New Item**
3. Enter name: `customer-service-cicd`
4. Select **Pipeline**
5. Click **OK**

### 2. Configure Pipeline

#### General Settings
- ✅ **Discard old builds**: Keep 10 builds
- ✅ **GitHub project**: https://github.com/your-org/customer-service
- ✅ **This project is parameterized** (optional)

#### Build Triggers
- ✅ **GitHub hook trigger for GITScm polling**
- ✅ **Poll SCM**: `H/5 * * * *` (every 5 minutes)

#### Pipeline Configuration
- **Definition**: Pipeline script from SCM
- **SCM**: Git
- **Repository URL**: https://github.com/your-org/customer-service
- **Credentials**: [your-git-credentials]
- **Branch Specifier**: `*/main`
- **Script Path**: `Jenkinsfile`

### 3. Webhook Configuration

#### GitHub Webhook (if using GitHub)
1. Go to your repository → Settings → Webhooks
2. Add webhook:
   ```
   Payload URL: https://your-jenkins.com/github-webhook/
   Content Type: application/json
   Events: Push events, Pull requests
   ```

## 📈 Monitoring & Notifications

### Slack Integration
1. Create Slack app and bot token
2. Add token to Jenkins credentials
3. Configure Slack plugin:
   ```
   Team Subdomain: your-team
   Integration Token: [slack-token-credential]
   Channel: #deployments
   ```

### Email Notifications
Configure email settings in **Manage Jenkins** → **Configure System**:
```
SMTP Server: smtp.company.com
SMTP Port: 587
Use SMTP Authentication: ✓
Username: jenkins@company.com
Password: [email-password]
```

## 🧪 Testing the Pipeline

### 1. Manual Trigger
- Go to your pipeline job
- Click **Build Now**
- Monitor the progress in **Blue Ocean** or **Console Output**

### 2. Git Push Trigger
```bash
# Make a small change and push
echo "# Test change" >> README.md
git add README.md
git commit -m "test: trigger Jenkins pipeline"
git push origin main
```

### 3. Verify Virtual Threads
Check the logs for virtual thread confirmation:
```
INFO c.k.m.config.VirtualThreadConfig - Creating Virtual Thread-based AsyncTaskExecutor
INFO c.k.m.service.ScheduledTaskService - 🧵 [THREAD-INFO] Health check running on: scheduled-task-0 (Virtual: true)
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Java 21 Not Found
```bash
Error: Could not find or load main class
```
**Solution**: Ensure JDK 21 is properly configured in Global Tool Configuration.

#### 2. Docker Permission Denied
```bash
Permission denied while trying to connect to Docker daemon
```
**Solution**: Add jenkins user to docker group and restart Jenkins.

#### 3. Virtual Threads Not Working
```bash
Virtual: false in logs
```
**Solution**: Verify `--enable-preview` flag is included in JAVA_OPTS.

#### 4. Maven Build Fails
```bash
Compilation failure with preview features
```
**Solution**: Add `--enable-preview` to Maven compiler args.

### Debug Commands

```bash
# Check Java version in Jenkins
java --version

# Check Maven configuration
mvn --version

# Test Docker access
docker ps

# Verify Kubernetes connectivity
kubectl cluster-info

# Check virtual thread support
java --list-modules | grep jdk.management
```

## 🎯 Best Practices

### Pipeline Optimization
- Use parallel stages for independent tasks
- Cache Maven dependencies between builds
- Use Docker layer caching
- Implement proper error handling

### Security
- Use least privilege principle for Jenkins user
- Rotate credentials regularly
- Scan Docker images for vulnerabilities
- Use secrets management for sensitive data

### Performance
- Optimize Docker image size with multi-stage builds
- Use virtual thread-specific JVM tuning
- Monitor resource usage and adjust limits
- Implement proper health checks

### Monitoring
- Set up alerts for failed deployments
- Monitor application metrics after deployment
- Track virtual thread performance metrics
- Use distributed tracing for microservices

## 📚 Additional Resources

- [Jenkins Pipeline Documentation](https://www.jenkins.io/doc/book/pipeline/)
- [Java 21 Virtual Threads Guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)
- [Spring Boot Actuator Endpoints](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Kubernetes Deployment Best Practices](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)

---

🎉 **Your Jenkins CI/CD pipeline is now ready to deploy your Virtual Thread-enabled Spring Boot application!**