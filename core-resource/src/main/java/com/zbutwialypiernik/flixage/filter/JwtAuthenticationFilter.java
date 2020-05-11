package com.zbutwialypiernik.flixage.filter;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
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
 *
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Claim name that represents id of user principal
     * @link https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-25#section-4.1.1
     */
    public static final String CLAIM_ID = "iss";
    /**
     * Claim name that represents username of user principal
     * @link https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-25#section-4.1.2
     */
    public static final String CLAIM_USERNAME = "sub";

    /**
     * Custom claim that represents role of user principal
     */
    public static final String CLAIM_ROLE = "role";

    private final JwtParser jwtParser;

    @Autowired
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtParser jwtParser) {
        super(authenticationManager);
        this.jwtParser = jwtParser;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(TOKEN_HEADER);

        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.replace(TOKEN_PREFIX, "");

        Authentication authenticationToken = getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String token) {
        try {
            Jws<Claims> claims = jwtParser.parseClaimsJws(token);

            String id = extractClaim(claims, CLAIM_ID);
            String username = extractClaim(claims, CLAIM_USERNAME);
            String role = extractClaim(claims, CLAIM_ROLE);

            return new JwtAuthenticationToken(id, username, Role.valueOf(role));
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException("Access token is expired");
        } catch (JwtException ex) {
            throw new AuthenticationException("Wrong authentication token");
        }
    }

    private String extractClaim(Jws<Claims> claims, String name) {
        return claims.getBody().get(name).toString();
    }

}
