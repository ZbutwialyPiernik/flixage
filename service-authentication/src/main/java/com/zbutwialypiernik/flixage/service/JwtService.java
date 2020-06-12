package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.config.JwtConfig;
import com.zbutwialypiernik.flixage.dto.authentication.AuthenticationResponse;
import com.zbutwialypiernik.flixage.entity.RefreshToken;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import com.zbutwialypiernik.flixage.repository.TokenRepository;
import com.zbutwialypiernik.flixage.util.KeyUtil;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtConfig config;

    private final PrivateKey privateKey;

    private final TokenRepository tokenRepository;
    private final DatabaseUserDetails userService;

    private final Clock clock;

    @Autowired
    public JwtService(TokenRepository tokenRepository,
                      DatabaseUserDetails userService,
                      JwtConfig config,
                      Clock clock) {
        this.clock = clock;
        this.config = config;
        this.tokenRepository = tokenRepository;
        this.userService = userService;
        this.privateKey = KeyUtil.getRsaPrivateKey(config.getPrivateKey());
    }

    /**
     * Creates new authentication based on
     * @param username
     * @param password
     *
     * @throws AuthenticationException when user is locked
     * @throws AuthenticationException when user has expired credentials
     * @throws AuthenticationException when user is disabled
     *
     * @return the authentication based on provided credentials
     */
    public AuthenticationResponse authenticate(String username, String password) {
        var user = userService.getUserByCredentials(username, password)
                .orElseThrow(() -> new AuthenticationException("Invalid Credentials"));

        checkForAbilityToLogin(user);

        var refreshToken = createRefreshToken(user);

        return new AuthenticationResponse(createAccessToken(user), refreshToken.getId(), config.getAccessTokenExpireTime());
    }

    /**
     *
     * @param refreshToken the valid refresh token
     *
     * @return the authentication based on refresh token
     */
    public AuthenticationResponse refreshAuthentication(String refreshToken) {
        var session = tokenRepository.findById(refreshToken).orElseThrow(() -> new AuthenticationException("Invalid Refresh Token"));

        if (session.isExpired(clock)) {
            throw new AuthenticationException("Refresh token is expired");
        }

        checkForAbilityToLogin(session.getUser());

        return new AuthenticationResponse(createAccessToken(session.getUser()), session.getId(), config.getAccessTokenExpireTime());
    }

    /**
     * Invalidates refresh token, this is equal to logout, but doesn't invalidate access tokens created by refresh token.
     * On front-end refresh token should be flushed with access token. This is drawback of using stateless authentication.
     *
     * @param refreshToken the refresh token to invalidate
     */
    public void invalidateToken(String refreshToken) {
        tokenRepository.deleteById(refreshToken);
    }

    private void checkForAbilityToLogin(User user) {
        if (user.isLocked()) {
            throw new AuthenticationException("User is locked");
        }

        if (user.isExpiredCredentials()) {
            throw new AuthenticationException("User has expired credentials, please change your password");
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
                .setExpiration(new Date(now + config.getAccessTokenExpireTime().toMillis()))
                .signWith(privateKey)
                .compact();
    }

    private RefreshToken createRefreshToken(User user) {
        if (tokenRepository.countByUserId(user.getId()) >= config.getMaxSessionCount()) {
            throw new AuthenticationException("Session count exceeded");
        }

        var refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setCreationTime(Instant.now(clock));

        tokenRepository.save(refreshToken);

        return refreshToken;
    }

}
