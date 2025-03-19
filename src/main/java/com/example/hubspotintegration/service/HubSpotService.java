package com.example.hubspotintegration.service;

import com.example.hubspotintegration.config.HubSpotConfig;
import com.example.hubspotintegration.exception.UserExistsException;
import com.example.hubspotintegration.model.Contact;
import com.example.hubspotintegration.model.HubSpotContactRequest;
import com.example.hubspotintegration.model.HubSpotContactResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.naming.AuthenticationException;
import java.util.*;

@Service
public class HubSpotService {
    private final HubSpotConfig config;
    private final TokenService tokenService;


    public HubSpotService(HubSpotConfig config, TokenService tokenService) {
        this.config = config;
        this.tokenService = tokenService;
    }

    public ResponseEntity<Void> getAuthorizationUrl() {
        String authUrl = UriComponentsBuilder.fromUriString("https://app.hubspot.com/oauth/authorize")
                .queryParam("client_id", config.getClientId())
                .queryParam("scope", config.getScopes())
                .queryParam("redirect_uri", config.getBaseUri().concat("/oauth-callback"))
                .build().toUriString();

        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, authUrl).build();
    }

    public ResponseEntity<String> exchangeCodeForToken(String authCode, HttpSession session) {
        return this.tokenService.exchangeCodeForToken(authCode, session);
    }

    public void refreshToken(HttpSession session) throws AuthenticationException {
        this.tokenService.refreshAccessToken(session.getId());
    }

    public List<Contact> getContacts(HttpSession session) throws AuthenticationException {
        String sessionId = session.getId();
        String accessToken = tokenService.getAccessToken(sessionId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<HubSpotContactResponse> response = restTemplate.exchange(
                "https://api.hubapi.com/contacts/v1/lists/all/contacts/all",
                HttpMethod.GET,
                requestEntity,
                HubSpotContactResponse.class
        );

        return Optional.ofNullable(response.getBody()).map(HubSpotContactResponse::getContacts).orElse(Collections.emptyList());
    }

    public String createContact(HttpSession session, HubSpotContactRequest contactRequest) throws Exception {
        String url = "https://api.hubapi.com/crm/v3/objects/contacts";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.tokenService.getAccessToken(session.getId()));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("properties", contactRequest);

        HttpEntity<Map> request = new HttpEntity<>(requestBody, headers);

        return executeWithExponentialBackoff(request, url);
    }

    private String executeWithExponentialBackoff(HttpEntity<Map> request, String url) throws Exception {
        int retryCount = 0;
        long waitTime = 1000;
        RestTemplate restTemplate = new RestTemplate();

        while (retryCount < 5) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        String.class);
                return response.getBody();

            } catch (HttpClientErrorException e) {
                HttpStatusCode statusCode = e.getStatusCode();

                if (statusCode == HttpStatus.TOO_MANY_REQUESTS) {
                    System.out.println("Rate limit atingido. Tentativa " + (retryCount + 1) + " de 5. Aguardando " + waitTime + "ms.");
                    Thread.sleep(waitTime);

                    retryCount++;
                    waitTime *= 2;
                    continue;
                }

                if (statusCode == HttpStatus.CONFLICT) {
                    Map responseBody = new ObjectMapper().readValue(e.getResponseBodyAsString(), Map.class);
                    throw new UserExistsException((String) responseBody.get("message"));
                }

                throw e;
            } catch (Exception e) {
                System.err.println("Erro ao tentar criar contato: " + e.getMessage());
                throw e;
            }
        }

        throw new Exception("Falha ao criar contato após várias tentativas devido a erro 429 (Rate Limit).");
    }

}