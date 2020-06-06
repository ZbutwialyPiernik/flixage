package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.ConflictException;
import com.zbutwialypiernik.flixage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final Clock clock;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;

        if (!userRepository.existsByUsernameIgnoreCase("admin")) {
            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setRole(Role.ADMIN);
            user.setName("Head Admin");
            user.setUsername("admin");
            user.setPassword("Passw0rd");

            registerUser(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
       return userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    /**
     * Creates new user, password has to be raw, not encrypted yet.
     *
     * @param user user to create.
     */
    public void registerUser(User user) {
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new ConflictException("Username is already taken by other user");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreationTime(Instant.now(clock));

        userRepository.save(user);
    }

    public Optional<User> getUserByCredentials(String username, String rawPassword) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);

        return user.filter(value -> passwordEncoder.matches(rawPassword, value.getPassword()));
    }

}
