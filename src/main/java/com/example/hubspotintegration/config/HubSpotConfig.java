package com.example.hubspotintegration.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class HubSpotConfig {

    @Value("${hubspot.client-id}")
    private String clientId;

    @Value("${hubspot.client-secret}")
    private String clientSecret;

    @Value("${hubspot.base-uri}")
    private String baseUri;

    @Value("${hubspot.scopes}")
    private String scopes;

    public String getScopes() {
        return scopes.replace(" ", "%20");
    }
}
