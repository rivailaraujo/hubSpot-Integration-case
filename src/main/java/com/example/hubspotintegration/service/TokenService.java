package com.example.hubspotintegration.service;

import com.example.hubspotintegration.config.HubSpotConfig;
import com.example.hubspotintegration.model.HubSpotTokenRequest;
import com.example.hubspotintegration.model.HubSpotTokenResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.naming.AuthenticationException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    private final HubSpotConfig config;
    private final Cache<String, String> accessTokenCache;
    private final Cache<String, String> refreshTokenStore;
    private final String tokenEndpoint = "https://api.hubapi.com/oauth/v1/token";

    public TokenService(HubSpotConfig config) {
        this.config = config;
        this.accessTokenCache = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();

        this.refreshTokenStore = CacheBuilder.newBuilder()
                .build();
    }

    public void saveTokens(String sessionId, String accessToken, String refreshToken) {
        accessTokenCache.put(sessionId, accessToken);
        refreshTokenStore.put(sessionId, refreshToken);
    }

    public String getAccessToken(String sessionId) throws AuthenticationException {
        String accessToken = accessTokenCache.getIfPresent(sessionId);
        if (accessToken == null) {
            refreshAccessToken(sessionId);
        }
        return accessTokenCache.getIfPresent(sessionId);
    }

    public String getRefreshToken(String sessionId) {
        return refreshTokenStore.getIfPresent(sessionId);
    }

    public ResponseEntity<String> exchangeCodeForToken(String authCode, HttpSession session) {
        String sessionId = session.getId();

        RestTemplate restTemplate = new RestTemplate();

        HubSpotTokenRequest requestBody = HubSpotTokenRequest.builder()
                .grantType("authorization_code")
                .clientSecret(config.getClientSecret())
                .clientId(config.getClientId())
                .redirectUri(config.getBaseUri().concat("/oauth-callback"))
                .code(authCode)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody.toFormData(), headers);

        ResponseEntity<HubSpotTokenResponse> response = restTemplate.exchange(
                this.tokenEndpoint,
                HttpMethod.POST,
                requestEntity,
                HubSpotTokenResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            if (response.getBody() != null) {
                String accessToken = response.getBody().getAccessToken();
                String refreshToken = response.getBody().getRefreshToken();
                saveTokens(sessionId, accessToken, refreshToken);
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falha na autenticação com o HubSpot.");
    }

    public void refreshAccessToken(String sessionId) throws AuthenticationException {
        String refreshToken = getRefreshToken(sessionId);

        if (refreshToken == null) {
            throw new AuthenticationException();
        }

        RestTemplate restTemplate = new RestTemplate();

        HubSpotTokenRequest requestBody = HubSpotTokenRequest.builder()
                .grantType("refresh_token")
                .clientSecret(config.getClientSecret())
                .clientId(config.getClientId())
                .refreshToken(refreshToken)
                .build();


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody.toFormData(), headers);

        ResponseEntity<HubSpotTokenResponse> response = restTemplate.exchange(this.tokenEndpoint, HttpMethod.POST, requestEntity, HubSpotTokenResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String newAccessToken = response.getBody().getAccessToken();
            String newRefreshToken = response.getBody().getRefreshToken();
            saveTokens(sessionId, newAccessToken, newRefreshToken);
        } else {
            throw new AuthenticationException("Falha ao renovar o token. Faça login novamente.");
        }
    }
}

