package com.example.hubspotintegration.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HubSpotContactRequest {
    private String email;
    private String firstname;
    private String lastname;
}