package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.ConflictException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.UserRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService extends QueryableService<User> {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder encoder, ImageFileService imageService) {
        super(repository, imageService);
        this.repository = repository;
        this.encoder = encoder;
    }

    /**
     * Creates new user, password has to be raw, not encrypted yet.
     *
     * @param user the user to create
     *
     * @throws ConflictException when username is taken by other user
     * @throws IllegalArgumentException when entity has set explicit id
     * @throws IllegalArgumentException when entity has set explicit thumbnail
     *
     * @return the created user
     */
    @Override
    public User create(User user) {
        if (isUsernameTaken(user.getUsername())) {
            throw new ConflictException("Username is already taken by other user");
        }

        if (user.getName() == null) {
            user.setName(user.getUsername());
        }

        user.setPassword(encoder.encode(user.getPassword()));

        return super.create(user);
    }

    /**
     * Updates user
     *
     * @param user the user to get updated
     *
     * @throws ConflictException when username is taken by other user
     * @throws ResourceNotFoundException when entity doesn't exists in database
     * @throws IllegalStateException when creation date is other than existing in database
     *
     * @return the updated user
     */
    @Override
    public User update(User user) {
        if (isUsernameTakenByOtherUser(user.getId(), user.getUsername())) {
            throw new ConflictException("Username is already taken by other user");
        }

        User oldUser = repository.findById(user.getId()).orElseThrow(ResourceNotFoundException::new);

        if (!oldUser.getPassword().equals(user.getPassword())) {
            user.setPassword(encoder.encode(user.getPassword()));
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
     * This method is mainly used during update of user entity. Unlike name, username
     * has to be unique, so we have to exclude user during update.
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
