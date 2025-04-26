package org.kpi.postservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.kpi.postservice.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    @Value("${user.service.host}")
    private String host;

    @Value("${system.username}")
    private String systemUsername;
    @Value("${system.password}")
    private String systemPassword;
    private SecretKey key;

    private final RestTemplate restTemplate;

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        setupJwtInterceptor();
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public User getUserByEmail(String email) {
        String url = host + "/email/" + email;

        User user;
        try {
            user = restTemplate.getForObject(url, User.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("User was not found with email: " + email);
        }

        if (user == null) {
            throw new IllegalStateException("User service returned null for id: " + email);
        }

        return user;
    }

    private void setupJwtInterceptor() {
        ClientHttpRequestInterceptor jwtInterceptor = (request, body, execution) -> {
            String token = generateToken(systemUsername);
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            log.debug("Added JWT token to request: {}", token);
            return execution.execute(request, body);
        };
        restTemplate.setInterceptors(Collections.singletonList(jwtInterceptor));
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
