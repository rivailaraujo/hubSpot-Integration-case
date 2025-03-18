package com.example.hubspotintegration.service;

import com.example.hubspotintegration.config.HubSpotConfig;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class HubSpotWebHookService {

    private final HubSpotConfig config;

    HubSpotWebHookService(HubSpotConfig config) {
        this.config = config;
    }

    public String calculateSignature(String payload) throws NoSuchAlgorithmException {
        String data = config.getClientSecret() + payload;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes());
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }
}
