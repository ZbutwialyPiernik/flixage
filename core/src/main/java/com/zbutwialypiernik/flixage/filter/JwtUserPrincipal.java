package com.zbutwialypiernik.flixage.filter;

import lombok.Data;
import lombok.Value;

import java.security.Principal;

@Data
public class JwtUserPrincipal implements Principal {

    private final String id;

    private final String name;

}
