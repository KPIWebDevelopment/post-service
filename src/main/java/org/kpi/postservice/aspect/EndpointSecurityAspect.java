package org.kpi.postservice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class EndpointSecurityAspect {

    private final RestTemplate restTemplate;

    public EndpointSecurityAspect(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Before("execution(* org.kpi.postservice.controller.*.*(..))")
    public void logBeforeEndpoint() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        log.debug("Request URI: " + request.getHeaders(HttpHeaders.ACCEPT));
        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        } else {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", jwtToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                "http://user-service:8080/auth/validate-request",
                HttpMethod.POST,
                entity,
                Boolean.class
        );

        if (response.getStatusCode().is4xxClientError()) {
            throw new SecurityException(response.getBody().toString());
        }
        else if (Boolean.FALSE.equals(response.getBody())){
            throw new SecurityException("JWT token validation failed with status: " + response.getStatusCode());
        }
    }
}
