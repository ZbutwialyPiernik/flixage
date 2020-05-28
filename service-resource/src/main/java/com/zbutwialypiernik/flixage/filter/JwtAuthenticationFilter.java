package com.zbutwialypiernik.flixage.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import com.zbutwialypiernik.flixage.exception.handler.ExceptionResponse;
import io.jsonwebtoken.*;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

/**
 * JWT Filter class for services other than Authentication.
 * Creates {@link JwtAuthenticationToken} and puts into Security Context if token is valid.
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";

    private final Clock clock;

    private final JwtAuthenticationParser parser;

    @Autowired
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtAuthenticationParser parser, Clock clock) {
        super(authenticationManager);
        this.clock = clock;
        this.parser = parser;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(TOKEN_HEADER);

        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.replace(TOKEN_PREFIX, "");

        try {
            JwtAuthenticationToken authenticationToken = parser.parseToken(token);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            printException(response, e.getMessage());
        }
    }

    private void printException(HttpServletResponse response, String message) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, HttpServletResponse.SC_UNAUTHORIZED, Instant.now(clock));

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        try {
            response.getWriter().write(exceptionResponse.toString());
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
