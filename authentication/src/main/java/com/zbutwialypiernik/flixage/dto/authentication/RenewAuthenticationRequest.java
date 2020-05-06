package com.zbutwialypiernik.flixage.dto.authentication;

import com.zbutwialypiernik.flixage.util.CommonRegex;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Value
public
class RenewAuthenticationRequest {

    @Pattern(regexp = CommonRegex.UUID)
    String refreshToken;

    @Pattern(regexp = CommonRegex.JWT_TOKEN)
    String accessToken;

}
