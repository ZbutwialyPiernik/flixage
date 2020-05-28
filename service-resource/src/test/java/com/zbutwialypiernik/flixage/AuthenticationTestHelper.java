package com.zbutwialypiernik.flixage;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;

import java.util.UUID;

public class AuthenticationTestHelper {

    public static JwtAuthenticationToken createMockAuthentication(Role role) {
        String id = UUID.randomUUID().toString();
        return new JwtAuthenticationToken(id, "mockuser#" + id, role);
    }

}
