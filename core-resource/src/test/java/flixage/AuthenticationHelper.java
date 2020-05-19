package flixage;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;

import java.util.UUID;

public class AuthenticationHelper {

    public JwtAuthenticationToken createMockAuthentication(Role role) {
        String id = UUID.randomUUID().toString();
        return new JwtAuthenticationToken(id, "user#" + id, Role.ADMIN);
    }

}
