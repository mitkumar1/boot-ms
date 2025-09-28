package com.kumar.mithlesh.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for KubernetesAwareService
 */
@ExtendWith(MockitoExtension.class)
class KubernetesAwareServiceTest {

    @Mock
    private RestTemplate loadBalancedRestTemplate;

    @Mock
    private DiscoveryClient discoveryClient;

    @InjectMocks
    private KubernetesAwareService kubernetesAwareService;

    @BeforeEach
    void setUp() {
        // Set the namespace and app name using reflection
        ReflectionTestUtils.setField(kubernetesAwareService, "kubernetesNamespace", "test-namespace");
        ReflectionTestUtils.setField(kubernetesAwareService, "applicationName", "customer-service-test");
    }

    @Test
    void testCallExternalService_Success() {
        // Given
        String serviceUrl = "http://external-service";
        String endpoint = "test";
        String expectedResponse = "Success response";

        when(loadBalancedRestTemplate.getForObject(serviceUrl + "/" + endpoint, String.class))
                .thenReturn(expectedResponse);

        // When
        String result = kubernetesAwareService.callExternalService(serviceUrl, endpoint);

        // Then
        assertEquals(expectedResponse, result);
        verify(loadBalancedRestTemplate).getForObject(serviceUrl + "/" + endpoint, String.class);
    }

    @Test
    void testCallExternalService_CircuitBreakerFallback() {
        // Given
        String serviceUrl = "http://external-service";
        String endpoint = "test";

        when(loadBalancedRestTemplate.getForObject(serviceUrl + "/" + endpoint, String.class))
                .thenThrow(new RestClientException("Service unavailable"));

        // When & Then - Should trigger fallback
        assertThrows(RestClientException.class, () -> 
            kubernetesAwareService.callExternalService(serviceUrl, endpoint));
    }

    @Test
    void testCallKubernetesService_Success() {
        // Given
        String serviceName = "user-service";
        String endpoint = "users";
        String expectedResponse = "Users data";
        String kubernetesUrl = "http://user-service.test-namespace.svc.cluster.local";

        when(loadBalancedRestTemplate.getForObject(kubernetesUrl + "/" + endpoint, String.class))
                .thenReturn(expectedResponse);

        // When
        String result = kubernetesAwareService.callKubernetesService(serviceName, endpoint);

        // Then
        assertEquals(expectedResponse, result);
        verify(loadBalancedRestTemplate).getForObject(kubernetesUrl + "/" + endpoint, String.class);
    }

    @Test
    void testDiscoverServices_Success() {
        // Given
        String serviceName = "user-service";
        ServiceInstance instance1 = mock(ServiceInstance.class);
        ServiceInstance instance2 = mock(ServiceInstance.class);
        List<ServiceInstance> expectedInstances = Arrays.asList(instance1, instance2);

        when(discoveryClient.getInstances(serviceName))
                .thenReturn(expectedInstances);

        // When
        List<ServiceInstance> result = kubernetesAwareService.discoverServices(serviceName);

        // Then
        assertEquals(2, result.size());
        assertEquals(expectedInstances, result);
        verify(discoveryClient).getInstances(serviceName);
    }

    @Test
    void testGetKubernetesInfo() {
        // When
        String result = kubernetesAwareService.getKubernetesInfo();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("customer-service-test"));
        assertTrue(result.contains("test-namespace"));
    }

    @Test
    void testAsyncCallWithTimeout() {
        // Given
        String serviceUrl = "http://async-service";

        // When
        CompletableFuture<String> result = kubernetesAwareService.asyncCallWithTimeout(serviceUrl);

        // Then
        assertNotNull(result);
        // Note: This test verifies the CompletableFuture is created properly
        // The actual execution happens asynchronously
    }

    @Test
    void testFallbackMethods() {
        // Test external service fallback
        String externalFallback = kubernetesAwareService.fallbackExternalService(
                "http://test", "endpoint", new RuntimeException("Test error"));
        assertNotNull(externalFallback);
        assertTrue(externalFallback.contains("fallback"));
        assertTrue(externalFallback.contains("temporarily unavailable"));

        // Test Kubernetes service fallback
        String k8sFallback = kubernetesAwareService.fallbackKubernetesService(
                "test-service", "endpoint", new RuntimeException("Test error"));
        assertNotNull(k8sFallback);
        assertTrue(k8sFallback.contains("fallback"));

        // Test health check fallback
        boolean healthFallback = kubernetesAwareService.fallbackHealthCheck(
                "test-service", new RuntimeException("Test error"));
        assertFalse(healthFallback);
    }
}