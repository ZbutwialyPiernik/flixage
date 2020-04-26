package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.AuthenticationResponse;
import com.zbutwialypiernik.flixage.service.JwtService;
import com.zbutwialypiernik.flixage.validator.password.ValidPassword;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final JwtService jwtService;

    public AuthenticationController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping
    public AuthenticationResponse authentication(@Valid @RequestBody AuthenticationRequest request) {
        return jwtService.authenticate(request.getUsername(), request.getPassword());
    }

    @PostMapping("/renew")
    public AuthenticationResponse renewToken(@Valid @RequestBody RenewAuthenticationRequest request) {
        return jwtService.regenerateAuthentication(request.getRefreshToken(), request.getAccessToken());
    }

    @PostMapping("/invalidate")
    public void invalidateToken(@Valid @RequestBody InvalidateTokenRequest request) {
        jwtService.invalidateToken(request.getRefreshToken());
    }

    @GetMapping("/public")
    public String getPublicKey() {
        return jwtService.getPublicKey();
    }

    @Value
    private static class AuthenticationRequest {

        @Size(min = 5, max = 32, message = "Length between 5-32")
        String username;

        @ValidPassword
        String password;

    }

    @Value
    private static class RenewAuthenticationRequest {

        String refreshToken;

        String accessToken;

    }

    @Value
    private static class InvalidateTokenRequest {

        String refreshToken;

    }

}
