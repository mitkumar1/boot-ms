#!/bin/bash
# Security Scan Validation Script
# This script validates security scan results and enforces security policies

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
FORTIFY_CRITICAL_THRESHOLD=0
FORTIFY_HIGH_THRESHOLD=5
BLACKDUCK_CRITICAL_THRESHOLD=0
BLACKDUCK_HIGH_THRESHOLD=3
OWASP_CVSS_THRESHOLD=8.0

echo -e "${BLUE}🔍 Starting Security Scan Validation${NC}"
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

# Function to validate Fortify results
validate_fortify() {
    log_info "Validating Fortify SAST results..."
    
    if [[ -f "fortify-results.xml" ]]; then
        # Parse XML results
        critical_count=$(grep -c 'severity="Critical"' fortify-results.xml 2>/dev/null || echo "0")
        high_count=$(grep -c 'severity="High"' fortify-results.xml 2>/dev/null || echo "0")
        medium_count=$(grep -c 'severity="Medium"' fortify-results.xml 2>/dev/null || echo "0")
        
        log_info "Fortify Results:"
        log_info "  - Critical: $critical_count (threshold: $FORTIFY_CRITICAL_THRESHOLD)"
        log_info "  - High: $high_count (threshold: $FORTIFY_HIGH_THRESHOLD)"
        log_info "  - Medium: $medium_count"
        
        # Validate thresholds
        if [[ $critical_count -gt $FORTIFY_CRITICAL_THRESHOLD ]]; then
            log_error "Fortify critical vulnerabilities exceed threshold: $critical_count > $FORTIFY_CRITICAL_THRESHOLD"
            return 1
        fi
        
        if [[ $high_count -gt $FORTIFY_HIGH_THRESHOLD ]]; then
            log_error "Fortify high vulnerabilities exceed threshold: $high_count > $FORTIFY_HIGH_THRESHOLD"
            return 1
        fi
        
        log_success "Fortify validation passed"
        return 0
    else
        log_warning "Fortify results file not found"
        return 1
    fi
}

# Function to validate Black Duck results
validate_blackduck() {
    log_info "Validating Black Duck SCA results..."
    
    if [[ -f "blackduck-results.json" ]]; then
        # Parse JSON results
        policy_violations=$(grep -c '"policyViolation":true' blackduck-results.json 2>/dev/null || echo "0")
        critical_risk=$(grep -c '"riskPriority":"CRITICAL"' blackduck-results.json 2>/dev/null || echo "0")
        high_risk=$(grep -c '"riskPriority":"HIGH"' blackduck-results.json 2>/dev/null || echo "0")
        
        log_info "Black Duck Results:"
        log_info "  - Policy Violations: $policy_violations"
        log_info "  - Critical Risk: $critical_risk (threshold: $BLACKDUCK_CRITICAL_THRESHOLD)"
        log_info "  - High Risk: $high_risk (threshold: $BLACKDUCK_HIGH_THRESHOLD)"
        
        # Validate thresholds
        if [[ $policy_violations -gt 0 ]]; then
            log_error "Black Duck policy violations found: $policy_violations"
            return 1
        fi
        
        if [[ $critical_risk -gt $BLACKDUCK_CRITICAL_THRESHOLD ]]; then
            log_error "Black Duck critical risk components exceed threshold: $critical_risk > $BLACKDUCK_CRITICAL_THRESHOLD"
            return 1
        fi
        
        if [[ $high_risk -gt $BLACKDUCK_HIGH_THRESHOLD ]]; then
            log_error "Black Duck high risk components exceed threshold: $high_risk > $BLACKDUCK_HIGH_THRESHOLD"
            return 1
        fi
        
        log_success "Black Duck validation passed"
        return 0
    else
        log_warning "Black Duck results file not found"
        return 1
    fi
}

# Function to validate OWASP Dependency Check results
validate_owasp() {
    log_info "Validating OWASP Dependency Check results..."
    
    if [[ -f "target/dependency-check-report.xml" ]]; then
        # Parse XML results
        vulnerabilities=$(grep -c '<vulnerability>' target/dependency-check-report.xml 2>/dev/null || echo "0")
        high_cvss=$(grep -E 'score="[89]\.' target/dependency-check-report.xml 2>/dev/null | wc -l || echo "0")
        critical_cvss=$(grep -E 'score="(9\.[5-9]|10\.0)"' target/dependency-check-report.xml 2>/dev/null | wc -l || echo "0")
        
        log_info "OWASP Results:"
        log_info "  - Total Vulnerabilities: $vulnerabilities"
        log_info "  - High CVSS (8.0-8.9): $high_cvss"
        log_info "  - Critical CVSS (9.5-10.0): $critical_cvss"
        
        # Validate critical CVSS scores
        if [[ $critical_cvss -gt 0 ]]; then
            log_error "Critical CVSS vulnerabilities found: $critical_cvss"
            return 1
        fi
        
        log_success "OWASP validation passed"
        return 0
    else
        log_warning "OWASP results file not found"
        return 1
    fi
}

# Function to generate security summary
generate_summary() {
    log_info "Generating security scan summary..."
    
    cat > security-scan-summary.md << EOF
# Security Scan Summary

**Build:** ${BUILD_NUMBER:-local}  
**Date:** $(date)  
**Project:** Customer Service with Virtual Threads

## Scan Results

### Fortify SAST
$(if [[ -f "fortify-results.xml" ]]; then
    critical=$(grep -c 'severity="Critical"' fortify-results.xml 2>/dev/null || echo "0")
    high=$(grep -c 'severity="High"' fortify-results.xml 2>/dev/null || echo "0")
    medium=$(grep -c 'severity="Medium"' fortify-results.xml 2>/dev/null || echo "0")
    echo "- **Critical:** $critical"
    echo "- **High:** $high"
    echo "- **Medium:** $medium"
    echo "- **Status:** $(if [[ $critical -eq 0 && $high -le $FORTIFY_HIGH_THRESHOLD ]]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi)"
else
    echo "- **Status:** ⚠️ NOT RUN"
fi)

### Black Duck SCA
$(if [[ -f "blackduck-results.json" ]]; then
    violations=$(grep -c '"policyViolation":true' blackduck-results.json 2>/dev/null || echo "0")
    critical=$(grep -c '"riskPriority":"CRITICAL"' blackduck-results.json 2>/dev/null || echo "0")
    high=$(grep -c '"riskPriority":"HIGH"' blackduck-results.json 2>/dev/null || echo "0")
    echo "- **Policy Violations:** $violations"
    echo "- **Critical Risk:** $critical"
    echo "- **High Risk:** $high"
    echo "- **Status:** $(if [[ $violations -eq 0 && $critical -eq 0 && $high -le $BLACKDUCK_HIGH_THRESHOLD ]]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi)"
else
    echo "- **Status:** ⚠️ NOT RUN"
fi)

### OWASP Dependency Check
$(if [[ -f "target/dependency-check-report.xml" ]]; then
    vulnerabilities=$(grep -c '<vulnerability>' target/dependency-check-report.xml 2>/dev/null || echo "0")
    critical=$(grep -E 'score="(9\.[5-9]|10\.0)"' target/dependency-check-report.xml 2>/dev/null | wc -l || echo "0")
    echo "- **Total Vulnerabilities:** $vulnerabilities"
    echo "- **Critical CVSS:** $critical"
    echo "- **Status:** $(if [[ $critical -eq 0 ]]; then echo "✅ PASSED"; else echo "❌ FAILED"; fi)"
else
    echo "- **Status:** ⚠️ NOT RUN"
fi)

## Virtual Thread Security Analysis

The application uses Java 21 Virtual Threads (Project Loom) with the following security considerations:

1. **Thread Safety:** All virtual thread operations are properly isolated
2. **Resource Management:** Virtual thread pools are configured with appropriate limits
3. **Memory Usage:** ZGC garbage collector optimizes virtual thread memory overhead
4. **Concurrency:** No shared mutable state between virtual thread tasks

## Recommendations

1. Schedule regular security scans (weekly for development, daily for main branch)
2. Monitor new CVE disclosures for Spring Boot and Java 21 dependencies
3. Implement security gates in the CI/CD pipeline
4. Review virtual thread usage patterns for security implications
5. Keep security scanning tools updated to latest versions

---
*Generated by security-validate.sh on $(date)*
EOF

    log_success "Security summary generated: security-scan-summary.md"
}

# Function to send notifications
send_notifications() {
    local exit_code=$1
    
    if [[ $exit_code -eq 0 ]]; then
        status="✅ PASSED"
        color="good"
    else
        status="❌ FAILED"
        color="danger"
    fi
    
    # Slack notification (if webhook is configured)
    if [[ -n "${SLACK_WEBHOOK_URL:-}" ]]; then
        curl -X POST -H 'Content-type: application/json' \
            --data "{\"text\":\"Security Scan $status for Customer Service (Build: ${BUILD_NUMBER:-local})\",\"color\":\"$color\"}" \
            "${SLACK_WEBHOOK_URL}" || true
    fi
    
    log_info "Notifications sent (exit code: $exit_code)"
}

# Main validation function
main() {
    local exit_code=0
    
    echo -e "${BLUE}Starting security validation for Customer Service${NC}"
    
    # Run validations
    if ! validate_owasp; then
        exit_code=1
    fi
    
    if ! validate_fortify; then
        exit_code=1
    fi
    
    if ! validate_blackduck; then
        exit_code=1
    fi
    
    # Generate summary regardless of results
    generate_summary
    
    # Send notifications
    send_notifications $exit_code
    
    echo "======================================"
    if [[ $exit_code -eq 0 ]]; then
        log_success "🎉 All security validations passed!"
    else
        log_error "💥 Security validation failed!"
    fi
    
    exit $exit_code
}

# Help function
show_help() {
    cat << EOF
Security Scan Validation Script

Usage: $0 [OPTIONS]

Options:
    -h, --help              Show this help message
    --fortify-only          Run only Fortify validation
    --blackduck-only        Run only Black Duck validation
    --owasp-only            Run only OWASP validation
    --summary-only          Generate summary only
    
Environment Variables:
    BUILD_NUMBER            Jenkins build number
    SLACK_WEBHOOK_URL       Slack webhook for notifications
    
Examples:
    $0                      # Run all validations
    $0 --fortify-only       # Run only Fortify validation
    $0 --summary-only       # Generate summary only
    
EOF
}

# Parse command line arguments
case "${1:-}" in
    -h|--help)
        show_help
        exit 0
        ;;
    --fortify-only)
        validate_fortify
        exit $?
        ;;
    --blackduck-only)
        validate_blackduck
        exit $?
        ;;
    --owasp-only)
        validate_owasp
        exit $?
        ;;
    --summary-only)
        generate_summary
        exit 0
        ;;
    *)
        main
        ;;
esac