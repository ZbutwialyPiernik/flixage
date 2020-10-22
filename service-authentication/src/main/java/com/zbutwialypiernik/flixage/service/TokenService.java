package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.config.JwtConfig;
import com.zbutwialypiernik.flixage.entity.RefreshToken;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import com.zbutwialypiernik.flixage.repository.TokenRepository;
import com.zbutwialypiernik.flixage.util.KeyUtil;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.security.PrivateKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenService {

    private final PrivateKey privateKey;

    private final JwtConfig config;
    private final TokenRepository tokenRepository;
    private final Clock clock;

    public TokenService(TokenRepository tokenRepository, JwtConfig config, Clock clock) {
        this.privateKey = KeyUtil.getRsaPrivateKey(config.getPrivateKey());
        this.tokenRepository = tokenRepository;

        this.config = config;
        this.clock = clock;
    }

    public String generateAccessToken(User user) {
        final long now = clock.millis();

        return Jwts.builder()
                .setIssuer(user.getId())
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + config.getAccessTokenExpireTime().toMillis()))
                .signWith(privateKey)
                .compact();
    }

    @Transactional
    public RefreshToken generateRefreshToken(User user) {
        // 0 or lower means that user can have unlimited amount of sessions at one time
        if (config.getMaxSessionCount() > 0 && tokenRepository.countByUserId(user.getId()) >= config.getMaxSessionCount()) {
            tokenRepository.deleteOldestToken(user.getId());
        }

        final var refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpireTime(config.getRefreshTokenExpireTime());
        refreshToken.setCreationTime(Instant.now(clock));

        tokenRepository.save(refreshToken);

        return refreshToken;
    }

    public Optional<RefreshToken> findById(String id) {
        return tokenRepository.findById(id);
    }

    /**
     * Removes refresh token, this is equal to logout, but doesn't invalidate access tokens created by refresh token.
     * On front-end refresh token should be flushed with access token. This is drawback of using stateless authentication.
     *
     * @param refreshToken the refresh token to invalidate
     */
    public void removeToken(String refreshToken) {
        tokenRepository.deleteById(refreshToken);
    }


}
