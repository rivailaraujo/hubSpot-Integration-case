package com.example.hubspotintegration.controller;

import com.example.hubspotintegration.service.HubSpotService;
import com.example.hubspotintegration.service.TokenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.naming.AuthenticationException;

@Controller
public class DebugController {

    private final TokenService tokenService;
    private final HubSpotService hubSpotService;

    public DebugController(TokenService tokenService, HubSpotService hubSpotService) {
        this.hubSpotService = hubSpotService;
        this.tokenService = tokenService;
    }

    @GetMapping("/")
    public String debugPage(HttpSession session, Model model) {
        try {
            model.addAttribute("accessToken", this.tokenService.getAccessToken(session.getId()));
            model.addAttribute("refreshToken", this.tokenService.getRefreshToken(session.getId()));
            model.addAttribute("status", "Conectado");
            model.addAttribute("contacts", this.hubSpotService.getContacts(session));

        } catch (AuthenticationException e) {
            model.addAttribute("accessToken", "Nenhum");
            model.addAttribute("refreshToken", "Nenhum");
            model.addAttribute("status", "Desconectado");
        }

        return "debug";
    }
}
