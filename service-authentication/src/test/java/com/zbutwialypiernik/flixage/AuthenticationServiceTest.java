package com.zbutwialypiernik.flixage;

import com.zbutwialypiernik.flixage.entity.RefreshToken;
import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import com.zbutwialypiernik.flixage.service.DatabaseUserDetails;
import com.zbutwialypiernik.flixage.service.AuthenticationService;
import com.zbutwialypiernik.flixage.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private DatabaseUserDetails userService;

    private AuthenticationService authenticationService;

    private final Clock clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        authenticationService = new AuthenticationService(userService, tokenService, clock);
    }

    @Test
    public void user_does_get_authentication_when_credentials_does_match_and_is_able_to_login() {
        var username = "username";
        var password = "password";

        var user = new User();
        user.setName("Test User");
        user.setRole(Role.USER);
        user.setId(UUID.randomUUID().toString());

        var refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID().toString());
        refreshToken.setExpireTime(Duration.ofDays(7));
        refreshToken.setCreationTime(clock.instant().minus(Duration.ofDays(1)));

        var accessToken = "token.tokenn.tokennn";

        when(tokenService.generateRefreshToken(user)).thenReturn(refreshToken);
        when(tokenService.generateAccessToken(user)).thenReturn(accessToken);
        when(userService.getUserByCredentials(username, password)).thenReturn(Optional.of(user));

        var authentication = authenticationService.authenticate(username, password);

        assertEquals(refreshToken.getId(), authentication.getRefreshToken());
        assertEquals(accessToken, authentication.getAccessToken());
        assertEquals(refreshToken.getExpireTime().toSeconds(), authentication.getExpireTime());
    }

    @Test
    public void user_does_not_get_authentication_when_credentials_does_not_match_any_user() {
        var username = "username";
        var password = "password";

        when(userService.getUserByCredentials(any(), any())).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authenticationService.authenticate(username, password));
    }

    @Test
    public void user_does_not_get_authentication_when_user_is_locked() {
        var username = "username";
        var password = "password";

        var user = new User();
        user.setLocked(true);

        when(userService.getUserByCredentials(username, password)).thenReturn(Optional.of(user));

        assertThrows(AuthenticationException.class, () -> authenticationService.authenticate(username, password));
    }

    @Test
    public void user_does_not_get_authentication_when_user_is_disabled() {
        var username = "username";
        var password = "password";

        var user = new User();
        user.setEnabled(false);

        when(userService.getUserByCredentials(username, password)).thenReturn(Optional.of(user));

        assertThrows(AuthenticationException.class, () -> authenticationService.authenticate(username, password));
    }

    @Test
    public void user_does_not_get_authentication_when_user_has_expired_credentials() {
        var username = "username";
        var password = "password";

        var user = new User();
        user.setExpiredCredentials(true);

        when(userService.getUserByCredentials(username, password)).thenReturn(Optional.of(user));

        assertThrows(AuthenticationException.class, () -> authenticationService.authenticate(username, password));
    }

    @Test
    public void user_does_not_get_refreshed_authentication_when_refresh_token_is_valid_and_not_expired() {
        var user = new User();
        user.setName("Test User");
        user.setRole(Role.USER);
        user.setId(UUID.randomUUID().toString());

        var refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpireTime(Duration.ofDays(7));
        refreshToken.setCreationTime(clock.instant().minus(1, ChronoUnit.HOURS));

        var accessToken = "token.tokenn.tokennn";

        when(tokenService.findById(refreshToken.getId())).thenReturn(Optional.of(refreshToken));
        when(tokenService.generateAccessToken(user)).thenReturn(accessToken);

        var authentication = authenticationService.refreshAuthentication(refreshToken.getId());

        assertEquals(refreshToken.getId(), authentication.getRefreshToken());
        assertEquals(accessToken, authentication.getAccessToken());
        assertEquals(refreshToken.getExpireTime(), authentication.getExpireTime());
    }

    @Test
    public void user_does_not_get_refreshed_authentication_when_refresh_token_is_expired() {
        var user = new User();
        user.setName("Test User");
        user.setRole(Role.USER);
        user.setId(UUID.randomUUID().toString());

        var refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpireTime(Duration.ofDays(7));
        refreshToken.setCreationTime(clock.instant().minus(Duration.ofDays(8)));

        when(tokenService.findById(refreshToken.getId())).thenReturn(Optional.of(refreshToken));

        assertThrows(AuthenticationException.class, () -> authenticationService.refreshAuthentication(refreshToken.getId()));
    }

    @Test
    public void user_does_not_get_refreshed_authentication_when_refresh_token_is_invalid() {
        var refreshToken = UUID.randomUUID().toString();
        
        when(tokenService.findById(any())).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authenticationService.refreshAuthentication(refreshToken));
    }

    @Test
    public void user_does_not_get_refreshed_authentication_when_user_is_locked() {
        var user = new User();
        user.setName("Test User");
        user.setRole(Role.USER);
        user.setId(UUID.randomUUID().toString());
        user.setLocked(true);

        var refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpireTime(Duration.ofSeconds(1234));
        refreshToken.setCreationTime(clock.instant().minus(1, ChronoUnit.HOURS));

        when(tokenService.findById(refreshToken.getId())).thenReturn(Optional.of(refreshToken));

        assertThrows(AuthenticationException.class, () -> authenticationService.refreshAuthentication(refreshToken.getId()));
    }

    @Test
    public void user_does_not_get_refreshed_authentication_when_user_has_expired_credentials() {
        var user = new User();
        user.setName("Test User");
        user.setRole(Role.USER);
        user.setId(UUID.randomUUID().toString());
        user.setExpiredCredentials(true);

        var refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpireTime(Duration.ofSeconds(1234));
        refreshToken.setCreationTime(clock.instant().minus(1, ChronoUnit.HOURS));

        when(tokenService.findById(refreshToken.getId())).thenReturn(Optional.of(refreshToken));

        assertThrows(AuthenticationException.class, () -> authenticationService.refreshAuthentication(refreshToken.getId()));
    }

    @Test
    public void user_does_not_get_refreshed_authentication_when_user_is_disabled() {
        var user = new User();
        user.setName("Test User");
        user.setRole(Role.USER);
        user.setId(UUID.randomUUID().toString());
        user.setEnabled(false);

        var refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpireTime(Duration.ofSeconds(1234));
        refreshToken.setCreationTime(clock.instant().minus(1, ChronoUnit.HOURS));

        when(tokenService.findById(refreshToken.getId())).thenReturn(Optional.of(refreshToken));

        assertThrows(AuthenticationException.class, () -> authenticationService.refreshAuthentication(refreshToken.getId()));
    }

}
