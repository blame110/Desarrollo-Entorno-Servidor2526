package com.calabozo.mapa.controller;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.calabozo.mapa.model.User;
import com.calabozo.mapa.repository.UserRepository;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String home() {
        logger.info("Cargando la web principial");
        logger.error("Es un mensaje de tipo errro ");
        System.out.println("Entra en la ruta princial");
        return "home";
    }

    @GetMapping("/login")
    public String login() {

        logger.info("Intentando logearnos");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            String email = principal.getAttribute("email");
            User user = userRepository.findByEmail(email).orElse(null);

            model.addAttribute("name", principal.getAttribute("name"));
            model.addAttribute("email", email);
            model.addAttribute("picture", principal.getAttribute("picture"));
            model.addAttribute("user", user);
        }
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            model.addAttribute("attributes", principal.getAttributes());
        }
        return "profile";
    }
}
