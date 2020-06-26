package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.config.JwtConfig;
import com.zbutwialypiernik.flixage.dto.authentication.AuthenticationResponse;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class AuthenticationService {

    private final DatabaseUserDetails userService;
    private final TokenService tokenService;

    private final Clock clock;

    @Autowired
    public AuthenticationService(DatabaseUserDetails userService,
                                 TokenService tokenService,
                                 Clock clock) {
        this.clock = clock;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    /**
     * Creates new authentication based on credentials
     * @param username the username of user
     * @param password the
     *
     * @throws AuthenticationException when user is locked
     * @throws AuthenticationException when user has expired credentials
     * @throws AuthenticationException when user is disabled
     * @throws AuthenticationException when credentials are invalid
     *
     * @return the authentication based on provided credentials
     */
    public AuthenticationResponse authenticate(String username, String password) {
        var user = userService.getUserByCredentials(username, password)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        checkForAbilityToLogin(user);

        var refreshToken = tokenService.generateRefreshToken(user);

        return new AuthenticationResponse(tokenService.generateAccessToken(user), refreshToken.getId(), refreshToken.getExpireTime().toSeconds());
    }

    /**
     *
     * @param refreshTokenId the valid refresh token
     *
     * @throws AuthenticationException when refresh token is invalid
     * @throws AuthenticationException when user is locked
     * @throws AuthenticationException when user has expired credentials
     * @throws AuthenticationException when user is disabled
     *
     * @return the authentication based on refresh token
     */
    @Transactional
    public AuthenticationResponse refreshAuthentication(String refreshTokenId) {
        var refreshToken = tokenService.findById(refreshTokenId).orElseThrow(() -> new AuthenticationException("Invalid refresh token"));

        checkForAbilityToLogin(refreshToken.getUser());

        if (clock.instant().isAfter(refreshToken.getExpireDate())) {
            tokenService.removeToken(refreshToken.getId());

            throw new AuthenticationException("Refresh token is expired");
        }

        return new AuthenticationResponse(tokenService.generateAccessToken(refreshToken.getUser()), refreshToken.getId(), refreshToken.getExpireTime().toSeconds());
    }

    public void invalidateToken(String refreshToken) {
        tokenService.removeToken(refreshToken);
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

}
