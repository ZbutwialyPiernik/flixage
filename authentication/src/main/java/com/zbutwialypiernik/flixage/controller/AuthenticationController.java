package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.authentication.AuthenticationRequest;
import com.zbutwialypiernik.flixage.dto.authentication.AuthenticationResponse;
import com.zbutwialypiernik.flixage.dto.authentication.RenewAuthenticationRequest;
import com.zbutwialypiernik.flixage.service.JwtService;
import com.zbutwialypiernik.flixage.validator.password.ValidPassword;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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

    @Value
    private static class InvalidateTokenRequest {

        @NotNull
        String refreshToken;

    }

}
