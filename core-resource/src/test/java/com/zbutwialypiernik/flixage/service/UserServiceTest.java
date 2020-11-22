package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.ConflictException;
import com.zbutwialypiernik.flixage.repository.UserRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PlaylistService playlistService;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    ImageFileService imageService;

    User user;

    Clock clock = Clock.fixed(Instant.parse("2020-04-25T00:00:00.00Z"), ZoneId.of("UTC"));

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        userService = new UserService(userRepository, passwordEncoder, imageService, playlistService);

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("Username");
        user.setPassword("RawPassword");
        user.setCreationTime(clock.instant());
    }

    @Test
    public void user_does_get_created() {
        when(userRepository.existsByUsernameIgnoreCase(user.getUsername())).thenReturn(false);

        user.setId(null);
        userService.create(user);

        verify(passwordEncoder).encode(any());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void user_does_get_created_when_username_is_not_taken() {
        when(userRepository.existsByUsernameIgnoreCase(user.getUsername())).thenReturn(false);

        user.setId(null);
        userService.create(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void user_does_not_get_created_when_username_is_taken() {
        when(userRepository.existsByUsernameIgnoreCase(user.getUsername())).thenReturn(true);

        Assertions.assertThrows(ConflictException.class, () -> userService.create(user));

        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void user_password_does_not_get_encrypted_during_update_when_is_same_as_in_database() {
        when(userRepository.findByUsernameIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.existsById(user.getId())).thenReturn(true);

        var newUser = new User();

        newUser.setId(user.getId());
        newUser.setUsername("NewUsername");
        newUser.setPassword(user.getPassword());
        newUser.setCreationTime(user.getCreationTime());

        userService.update(newUser);

        verify(passwordEncoder, times(0)).encode(any());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    public void user_password_does_get_encrypted_during_update_when_is_other_than_in_database() {
        when(userRepository.findByUsernameIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.existsById(user.getId())).thenReturn(true);

        var newUser = new User();

        newUser.setId(user.getId());
        newUser.setUsername("NewUsername");
        newUser.setPassword("NewRawPassword");
        newUser.setCreationTime(user.getCreationTime());

        userService.update(newUser);

        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    public void returns_true_when_username_is_taken_by_other_user() {
        when(userRepository.findByUsernameIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));

        Assertions.assertTrue(userService.isUsernameTakenByOtherUser(UUID.randomUUID().toString(), user.getUsername()));
    }

    @Test
    public void returns_false_when_username_is_taken_by_same_user() {
        when(userRepository.findByUsernameIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));

        Assertions.assertFalse(userService.isUsernameTakenByOtherUser(user.getId(), user.getUsername()));
    }

}
