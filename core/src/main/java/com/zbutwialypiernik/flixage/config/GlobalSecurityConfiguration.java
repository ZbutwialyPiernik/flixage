package com.zbutwialypiernik.flixage.config;

import org.passay.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

/*
* Shared configuration security between projects, to avoid code duplication and mistakes.
*/
@Configuration
public class GlobalSecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordValidator passwordValidator() {
        return new PasswordValidator(Arrays.asList(
                new LengthRule(8, 128),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new WhitespaceRule()
                ));
    }

}
