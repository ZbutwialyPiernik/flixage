package com.zbutwialypiernik.flixage.util;

import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * Method to check if current user is logged in, in case of anonymous authentication returns false.
     */
    public static boolean isUserLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
               SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
               SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken;
    }

}
