package com.zbutwialypiernik.flixage.filter;

import com.zbutwialypiernik.flixage.entity.Role;
import lombok.Data;
import lombok.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Data
public class JwtAuthenticationToken implements Authentication {

    private final JwtUserPrincipal principal;

    private final Role role;

    private boolean authenticated = true;

    public JwtAuthenticationToken(String id, String name, Role role) {
        this.principal = new JwtUserPrincipal(id, name);
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role.toAuthority());
    }

    @Override
    public String getName() {
        return principal.getName();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

}
