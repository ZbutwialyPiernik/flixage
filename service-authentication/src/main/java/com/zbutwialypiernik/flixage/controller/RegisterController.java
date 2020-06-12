package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.service.DatabaseUserDetails;
import com.zbutwialypiernik.flixage.validator.ValidPassword;
import lombok.Value;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class RegisterController {

    private final DatabaseUserDetails userService;

    private final MapperFacade mapper;

    public RegisterController(DatabaseUserDetails userService, MapperFacade mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping
    public void registerUser(@Valid RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password does not match");
        }

        User user = mapper.map(request, User.class);
        user.setName(user.getUsername());

        userService.registerUser(user);
    }

    @Value
    private static class RegisterRequest {

        String username;

        @ValidPassword
        String password;

        String confirmPassword;

    }

}
