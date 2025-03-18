package com.example.hubspotintegration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HubSpotContactResponse {
    @JsonProperty("contacts")
    private List<Contact> contacts;
}

