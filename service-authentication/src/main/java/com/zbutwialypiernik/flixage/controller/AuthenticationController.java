package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.authentication.AuthenticationRequest;
import com.zbutwialypiernik.flixage.dto.authentication.AuthenticationResponse;
import com.zbutwialypiernik.flixage.dto.authentication.InvalidateTokenRequest;
import com.zbutwialypiernik.flixage.dto.authentication.RenewAuthenticationRequest;
import com.zbutwialypiernik.flixage.service.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
        return jwtService.refreshAuthentication(request.getRefreshToken());
    }

    @PostMapping("/invalidate")
    public void invalidateToken(@Valid @RequestBody InvalidateTokenRequest request) {
        jwtService.invalidateToken(request.getRefreshToken());
    }

}
