package com.zbutwialypiernik.flixage.dto.authentication;

import com.zbutwialypiernik.flixage.validator.ValidPassword;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Size;

@Value
@NoArgsConstructor(force = true)
public class AuthenticationRequest {

    @Size(min = 5, max = 32, message = "Length between 5-32")
    String username;

    @ValidPassword
    String password;

}
