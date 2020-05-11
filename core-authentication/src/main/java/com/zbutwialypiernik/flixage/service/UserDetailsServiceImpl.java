package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.ConflictException;
import com.zbutwialypiernik.flixage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final Clock clock;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
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
        user.setLastUpdateTime(user.getCreationTime());

        userRepository.save(user);
    }

    public Optional<User> getUserByCredentials(String username, String rawPassword) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(username);

        return user.filter(value -> passwordEncoder.matches(rawPassword, value.getPassword()));
    }

}
