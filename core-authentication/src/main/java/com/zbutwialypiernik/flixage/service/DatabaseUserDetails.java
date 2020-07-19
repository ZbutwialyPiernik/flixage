package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.ConflictException;
import com.zbutwialypiernik.flixage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DatabaseUserDetails implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseUserDetails(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

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
     * @param user the user to create.
     */
    public void registerUser(User user) {
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new ConflictException("Username is already taken by other user");
        }

        user.setId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getName() == null) {
            user.setName(user.getUsername());
        }

        userRepository.save(user);
    }

    /**
     * Finds user that matches credentials
     *
     * @param username the username of user
     * @param rawPassword the unencrypted password
     * @return the optional of user
     */
    public Optional<User> getUserByCredentials(String username, String rawPassword) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);

        return user.filter(value -> passwordEncoder.matches(rawPassword, value.getPassword()));
    }

}
