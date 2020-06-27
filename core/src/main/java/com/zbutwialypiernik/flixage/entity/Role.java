package com.zbutwialypiernik.flixage.entity;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    USER, ADMIN;

    public SimpleGrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority( "ROLE_" + name());
    }

}
