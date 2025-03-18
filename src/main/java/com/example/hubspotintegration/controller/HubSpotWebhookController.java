package com.example.hubspotintegration.controller;

import com.example.hubspotintegration.service.HubSpotWebHookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class HubSpotWebhookController {

    SimpMessagingTemplate messagingTemplate;
    HubSpotWebHookService hubSpotWebHookService;

    HubSpotWebhookController(SimpMessagingTemplate messagingTemplate, HubSpotWebHookService hubSpotWebHookService) {
        this.messagingTemplate = messagingTemplate;
        this.hubSpotWebHookService = hubSpotWebHookService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("X-HubSpot-Signature") String signature) {
        try {
            String calculatedSignature = this.hubSpotWebHookService.calculateSignature(payload);
            if (calculatedSignature.equals(signature)) {
                messagingTemplate.convertAndSend("/topic/webhook", "Novo webhook recebido!");
                return ResponseEntity.ok("Webhook processado com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Assinatura inválida.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no processamento do webhook.");
        }
    }
}