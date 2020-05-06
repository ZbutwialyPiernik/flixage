package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.dto.authentication.AuthenticationResponse;
import com.zbutwialypiernik.flixage.config.JwtConfig;
import com.zbutwialypiernik.flixage.entity.RefreshToken;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import com.zbutwialypiernik.flixage.repository.TokenRepository;
import com.zbutwialypiernik.flixage.util.KeyUtil;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class JwtService {

    private final JwtConfig config;
    private final JwtParser parser;

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    private final TokenRepository tokenRepository;
    private final UserService userService;

    private final Clock clock;

    @Autowired
    public JwtService(TokenRepository tokenRepository,
                      UserService userService,
                      JwtConfig config,
                      Clock clock) {
        this.clock = clock;
        this.config = config;
        this.tokenRepository = tokenRepository;
        this.userService = userService;

        this.privateKey = KeyUtil.getRsaPrivateKey(config.getPrivateKey());
        this.publicKey = KeyUtil.getRsaPublicKey(config.getPublicKey());

        this.parser = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build();
    }

    public AuthenticationResponse authenticate(String username, String password) {
        User user = userService.getUserByCredentials(username, password)
                .orElseThrow(() -> new AuthenticationException("Invalid Credentials"));

        checkForAbilityToLogin(user);

        RefreshToken refreshToken = createRefreshToken(user);

        return new AuthenticationResponse(createAccessToken(user), refreshToken.getId(), config.getAccessTokenExpireTime());
    }

    public AuthenticationResponse regenerateAuthentication(String refreshToken, String expiredAccessToken) {
        RefreshToken session = tokenRepository.findById(refreshToken).orElseThrow(() -> new AuthenticationException("Invalid Refresh Token"));

        if (session.isExpired(clock)) {
            throw new AuthenticationException("Refresh token is expired");
        }

        try {
            String username = parser.parseClaimsJws(expiredAccessToken).getBody().getSubject();

            if (!username.equals(session.getUser().getUsername())) {
                throw new AuthenticationException("Invalid access token");
            }
        } catch (JwtException e) {
            e.printStackTrace();
            throw new AuthenticationException("Invalid access token");
        }

        checkForAbilityToLogin(session.getUser());

        return new AuthenticationResponse(createAccessToken(session.getUser()), session.getId(), config.getAccessTokenExpireTime());
    }

    public void invalidateToken(String refreshToken) {
        tokenRepository.deleteById(refreshToken);
    }

    private void checkForAbilityToLogin(User user) {
        if (user.isLocked()) {
            throw new AuthenticationException("User is locked");
        }

        if (user.isExpiredCredentials()) {
            throw new AuthenticationException("User has expired credentials");
        }

        if (!user.isEnabled()) {
            throw new AuthenticationException("User is disabled");
        }
    }

    private String createAccessToken(User user) {
        long now = clock.millis();
        
        return Jwts.builder()
                .setIssuer(user.getId())
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + config.getAccessTokenExpireTime() * 1000))
                .signWith(privateKey)
                .compact();
    }

    private RefreshToken createRefreshToken(User user) {
        if (tokenRepository.countByUserId(user.getId()) >= config.getMaxSessionCount()) {
            throw new AuthenticationException("Session count exceeded");
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpireTime(config.getRefreshTokenExpireTime());
        refreshToken.setCreationTime(LocalDateTime.now(clock));
        refreshToken.setLastUpdateTime(LocalDateTime.now(clock));

        tokenRepository.save(refreshToken);

        return refreshToken;
    }

}
