package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.service.UserService;
import com.zbutwialypiernik.flixage.validator.password.ValidPassword;
import lombok.Value;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class RegisterController {

    private final UserService userService;

    private final MapperFacade mapper;

    public RegisterController(UserService userService, MapperFacade mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity registerUser(@Valid RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Password does not match");
        }

        User user = mapper.map(request, User.class);
        user.setName(user.getUsername());

        userService.create(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Value
    private static class RegisterRequest {

        String username;

        @ValidPassword
        String password;

        String confirmPassword;

    }


}
