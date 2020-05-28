package com.zbutwialypiernik.flixage.security;

import com.zbutwialypiernik.flixage.entity.Role;
import org.springframework.security.core.Authentication;

public interface AbstractAuthentication extends Authentication {

    String getId();

    Role getRole();

}
