package com.zbutwialypiernik.flixage.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import com.zbutwialypiernik.flixage.exception.handler.ExceptionResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT Filter class for services other than Authentication.
 * Creates {@link JwtAuthenticationToken} and puts into Security Context if token is valid.
 */
@Log4j2
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    public static final String TOKEN_PREFIX = "Bearer ";

    private final JwtAuthenticationParser parser;

    private final ObjectMapper objectMapper;

    @Autowired
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtAuthenticationParser parser, ObjectMapper objectMapper) {
        super(authenticationManager);
        this.parser = parser;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.replace(TOKEN_PREFIX, "");

        try {
            logger.debug("Request issued with token: " + token);

            JwtAuthenticationToken authenticationToken = parser.parseToken(token);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            printException(response, e.getMessage());
        }
    }

    private void printException(HttpServletResponse response, String message) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(message, HttpServletResponse.SC_UNAUTHORIZED);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            objectMapper.writeValue(response.getWriter(), exceptionResponse);
            response.getWriter().flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }

}
