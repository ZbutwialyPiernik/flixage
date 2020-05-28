package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.ConflictException;
import com.zbutwialypiernik.flixage.repository.UserRepository;
import com.zbutwialypiernik.flixage.service.file.ThumbnailFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.util.Optional;

@Service
public class UserService extends QueryableService<User> {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder encoder, ThumbnailFileService thumbnailService, Clock clock) throws IOException {
        super(repository, thumbnailService, clock);
        this.repository = repository;
        this.encoder = encoder;

        if (!isUsernameTaken("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setName("Super Admin");
            admin.setPassword("Passw0rd");
            admin.setRole(Role.ADMIN);
            create(admin);
        }
    }

    /**
     * Creates new user, password has to be raw, not encrypted yet.
     * @throws ConflictException when username is taken by other user
     * @param user the user to create
     * @return the created user
     */
    public User create(User user) {
        if (isUsernameTaken(user.getUsername())) {
            throw new ConflictException("Username is already taken by other user");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        return super.create(user);
    }

    /**
     * Updates user
     * @throws ConflictException when username is taken by other user
     * @param user the user to get updated
     * @return the updated user
     */
    public User update(User user) {
        if (isUsernameTakenByOtherUser(user.getId(), user.getUsername())) {
            throw new ConflictException("Username is already taken by other user");
        }

        return super.update(user);
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

}
