package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.Thumbnail;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.ConflictException;
import com.zbutwialypiernik.flixage.repository.ThumbnailStore;
import com.zbutwialypiernik.flixage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService extends QueryableService<User> {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final ThumbnailStore store;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder encoder, ThumbnailStore store, Clock clock) throws IOException {
        super(repository, store, clock);
        this.repository = repository;
        this.encoder = encoder;
        this.store = store;

        if (isUsernameTaken("admin")) {
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setName("John Wick");
        admin.setPassword("Passw0rd");
        admin.setRole(Role.ADMIN);
        create(admin);

        for (int i = 0; i < 100; i++) {
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();

            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            User newUser = new User();
            newUser.setRole(Role.USER);
            newUser.setUsername(generatedString);
            newUser.setName(generatedString + "name");
            newUser.setPassword("Passw0rd");
            create(newUser);
        }
    }

    public Page<User> findByUsername(String query, int page, int size) {
        return repository.findByUsernameContainingIgnoreCase(query, PageRequest.of(page, size));
    }

    /**
     * Creates new user, password has to be raw, not encrypted yet.
     *
     * @param user user to create.
     */
    public User create(User user) {
        if (isUsernameTaken(user.getUsername())) {
            throw new ConflictException("Username is already taken by other user");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        return super.create(user);
    }

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
