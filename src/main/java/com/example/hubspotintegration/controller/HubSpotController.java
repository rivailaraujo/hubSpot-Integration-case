package com.example.hubspotintegration.controller;

import com.example.hubspotintegration.exception.UserExistsException;
import com.example.hubspotintegration.model.Contact;
import com.example.hubspotintegration.model.HubSpotContactRequest;
import com.example.hubspotintegration.model.HubSpotContactResponse;
import com.example.hubspotintegration.service.HubSpotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
public class HubSpotController {

    private final HubSpotService hubSpotService;

    public HubSpotController(HubSpotService hubSpotService) {
        this.hubSpotService = hubSpotService;
    }

    @GetMapping("/install")
    public ResponseEntity<Void> installApp() {
        return this.hubSpotService.getAuthorizationUrl();
    }

    @GetMapping("/oauth-callback")
    public ResponseEntity<String> handleOAuthCallback(@RequestParam("code") String authCode, HttpSession session) {
        return this.hubSpotService.exchangeCodeForToken(authCode, session);
    }

    @PostMapping("/contact")
    public ResponseEntity<String> createContact(HttpSession session, @RequestBody HubSpotContactRequest contactRequest) {
        try {
            String response = hubSpotService.createContact(session, contactRequest);
            return ResponseEntity.ok(response);
        } catch (UserExistsException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar contato: " + e.getMessage());
        }
    }

    @GetMapping("/contacts")
    public List<Contact> getContacts(HttpSession session) throws AuthenticationException {
        return this.hubSpotService.getContacts(session);
    }

    @GetMapping("/refresh-token")
    public void refreshToken(HttpSession session) throws AuthenticationException {
        this.hubSpotService.refreshToken(session);
    }
}