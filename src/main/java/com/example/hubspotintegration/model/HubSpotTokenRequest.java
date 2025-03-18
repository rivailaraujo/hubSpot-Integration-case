package com.example.hubspotintegration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@Setter
@Builder
public class HubSpotTokenRequest {

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("redirect_uri")
    private String redirectUri;

    @JsonProperty("code")
    private String code;


    public MultiValueMap<String, String> toFormData() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        if (grantType != null) formData.add("grant_type", grantType);
        if (clientId != null) formData.add("client_id", clientId);
        if (clientSecret != null) formData.add("client_secret", clientSecret);
        if (refreshToken != null) formData.add("refresh_token", refreshToken);
        if (redirectUri != null) formData.add("redirect_uri", redirectUri);
        if (code != null) formData.add("code", code);
        return formData;
    }

}
