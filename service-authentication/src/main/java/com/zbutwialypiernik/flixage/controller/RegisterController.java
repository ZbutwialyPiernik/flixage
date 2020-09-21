package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.authentication.AuthenticationResponse;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.service.AuthenticationService;
import com.zbutwialypiernik.flixage.service.DatabaseUserDetails;
import com.zbutwialypiernik.flixage.validator.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/authentication")
public class RegisterController {

    private final AuthenticationService authenticationService;
    private final DatabaseUserDetails userService;

    private final MapperFacade mapper;

    public RegisterController(AuthenticationService authenticationService, DatabaseUserDetails userService, MapperFacade mapper) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public AuthenticationResponse registerUser(@Valid @RequestBody RegisterRequest request) {
        User user = mapper.map(request, User.class);
        
        userService.registerUser(user);

        /* user object will have encoded password, we need to use raw one */
        return authenticationService.authenticate(user.getUsername(), request.getPassword());
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    protected static class RegisterRequest {

        @NotNull(message = "Username cannot be null")
        @Length(min = 5, max = 32, message = "Username length should be between 5 and 32")
        String username;

        @ValidPassword
        String password;

    }

}
