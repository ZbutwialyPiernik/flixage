package com.zbutwialypiernik.flixage;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;

import java.util.UUID;

public class AuthenticationTestHelper {

    public static JwtAuthenticationToken createMockAuthentication(User user) {
        return new JwtAuthenticationToken(user.getId(), user.getName(), user.getRole());
    }

    public static JwtAuthenticationToken createMockAuthentication(Role role) {
        var id = UUID.randomUUID().toString();

        return new JwtAuthenticationToken(id, "mockuser" + id.substring(0, 6), role);
    }

}
