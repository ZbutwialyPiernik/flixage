package com.zbutwialypiernik.flixage.filter;

import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationParser {

    /**
     * Claim name that represents id of user principal
     *
     * @link https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-25#section-4.1.1
     */
    public static final String CLAIM_ID = "iss";
    /**
     * Claim name that represents username of user principal
     *
     * @link https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-25#section-4.1.2
     */
    public static final String CLAIM_USERNAME = "sub";

    /**
     * Custom claim that represents role of user principal
     */
    public static final String CLAIM_ROLE = "role";

    private final JwtParser jwtParser;

    @Autowired
    public JwtAuthenticationParser(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    /**
     * @param token the Jwt token
     * @return the authentication based on token claims
     * @throws AuthenticationException when token is invalid or expired
     */
    public JwtAuthenticationToken parseToken(String token) {
        try {
            Jws<Claims> claims = jwtParser.parseClaimsJws(token);

            String id = extractClaim(claims, CLAIM_ID);
            String username = extractClaim(claims, CLAIM_USERNAME);
            String role = extractClaim(claims, CLAIM_ROLE);

            return new JwtAuthenticationToken(id, username, Role.valueOf(role));
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException("Token is expired");
        }  catch (JwtException e) {
            throw new AuthenticationException("Token is invalid");
        }
    }

    private String extractClaim(Jws<Claims> claims, String name) {
        return claims.getBody().get(name).toString();
    }

}
