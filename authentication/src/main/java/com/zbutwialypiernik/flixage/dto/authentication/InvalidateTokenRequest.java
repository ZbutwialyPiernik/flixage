package com.zbutwialypiernik.flixage.dto.authentication;

import com.zbutwialypiernik.flixage.util.CommonRegex;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Pattern;

@Value
@NoArgsConstructor(force = true)
public class InvalidateTokenRequest {

    @Pattern(regexp = CommonRegex.UUID)
    String refreshToken;

}