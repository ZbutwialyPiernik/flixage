package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.exception.ConflictException;
import com.zbutwialypiernik.flixage.repository.ThumbnailStore;
import com.zbutwialypiernik.flixage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserService extends QueryableService<User> {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder encoder, ThumbnailStore<User> store, Clock clock) {
        super(repository, store, clock);
        this.repository = repository;
        this.encoder = encoder;
    }

    public Page<User> findByUsername(String query, int page, int size) {
        return repository.findByUsernameContainingIgnoreCase(query, PageRequest.of(page, size));
    }

    /**
     * Creates new user, password has to be raw, not encrypted yet.
     *
     * @param user user to create.
     */
    public void create(User user) {
        if (isUsernameTaken(user.getUsername())) {
            throw new ConflictException("Username is already taken by other user");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        super.create(user);
    }

    public void update(User user) {
        if (isUsernameTakenByOtherUser(user.getId(), user.getUsername())) {
            throw new ConflictException("Username is already taken by other user");
        }

        super.update(user);
    }

    /**
     * Checks if username is not taken by any user, ignoring case.
     *
     * @param username username to check
     */
    public boolean isUsernameTaken(String username) {
        return repository.existsByUsernameIgnoreCase(username);
    }

    /**
     * Checks if username is not taken by other user, ignoring case.
     *
     * @param excludedUserId user id to exclude from checking
     * @param username username to check
     */
    public boolean isUsernameTakenByOtherUser(String excludedUserId, String username) {
        Optional<User> userFound = findByUsername(username);

        return userFound.isPresent() && !userFound.get().getId().equals(excludedUserId);
    }

    /**
     * Finds single user by username, ignoring case.
     *
     * @param username username to find
     */
    public Optional<User> findByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username);
    }

    public Optional<User> getUserByCredentials(String username, String rawPassword) {
        Optional<User> user = findByUsername(username);

        return user.filter(value -> encoder.matches(rawPassword, value.getPassword()));
    }

}
