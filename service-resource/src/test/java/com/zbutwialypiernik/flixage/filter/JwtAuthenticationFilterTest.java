package com.zbutwialypiernik.flixage.filter;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;

public class JwtAuthenticationFilterTest {

    // Spring boot mocks
    @Mock
    HttpServletRequest request;
    @Spy
    HttpServletResponse response;
    @Spy
    FilterChain chain;
    @Spy
    SecurityContext securityContext;
    @Mock
    AuthenticationManager authenticationManager;

    JsonMapper jsonMapper = new JsonMapper();

    @Mock
    JwtAuthenticationParser parser;

    JwtAuthenticationFilter filter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        filter = new JwtAuthenticationFilter(authenticationManager, parser, jsonMapper, Clock.systemUTC());
    }

    /**
     * @param tokenWithPrefix token passed to request, by default prefix should be Bearer
     * @param expectedAuthentication null if user should not get authentication, else expected mocked value
     */
    public void test(String tokenWithPrefix, JwtAuthenticationToken expectedAuthentication) throws ServletException, IOException {
        Mockito.when(request.getHeader(JwtAuthenticationFilter.TOKEN_HEADER)).thenReturn(tokenWithPrefix);

        if (expectedAuthentication != null) {
            Mockito.when(parser.parseToken(Mockito.any())).thenReturn(expectedAuthentication);
        }

        SecurityContextHolder.setContext(securityContext);

        filter.doFilter(request, response, chain);

        if (expectedAuthentication != null) {
            Mockito.verify(securityContext, Mockito.times(1)).setAuthentication(expectedAuthentication);
            Mockito.verify(chain, Mockito.times(1)).doFilter(request, response);
        } else {
            Mockito.verify(chain, Mockito.times(0)).doFilter(request, response);
        }
    }

    @Test
    public void when_authentication_token_is_not_included_then_user_does_not_get_authentication() throws ServletException, IOException {
        test(null,
                null);
    }

    @Test
    public void when_token_has_no_prefix_then_user_does_not_get_authentication() throws ServletException, IOException {
        // Invalid token because does not start with prefix
        test("eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.hZnl5amPk_I3tb4O-Otci_5XZdVWhPlFyVRvcqSwnDo_srcysDvhhKOD01DigPK1lJvTSTolyUgKGtpLqMfRDXQlekRsF4XhAjYZTmcynf-C-6wO5EI4wYewLNKFGGJzHAknMgotJFjDi_NCVSjHsW3a10nTao1lB82FRS305T226Q0VqNVJVWhE4G0JQvi2TssRtCxYTqzXVt22iDKkXeZJARZ1paXHGV5Kd1CljcZtkNZYIGcwnj65gvuCwohbkIxAnhZMJXCLaVvHqv9l-AAUV7esZvkQR1IpwBAiDQJh4qxPjFGylyXrHMqh5NlT_pWL2ZoULWTg_TJjMO9TuQ",
                null);
    }

    @Test
    public void when_token_is_valid_then_user_gets_authentication() throws ServletException, IOException {
        test(JwtAuthenticationFilter.TOKEN_PREFIX + "eyJhbGciOiJQUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.hZnl5amPk_I3tb4O-Otci_5XZdVWhPlFyVRvcqSwnDo_srcysDvhhKOD01DigPK1lJvTSTolyUgKGtpLqMfRDXQlekRsF4XhAjYZTmcynf-C-6wO5EI4wYewLNKFGGJzHAknMgotJFjDi_NCVSjHsW3a10nTao1lB82FRS305T226Q0VqNVJVWhE4G0JQvi2TssRtCxYTqzXVt22iDKkXeZJARZ1paXHGV5Kd1CljcZtkNZYIGcwnj65gvuCwohbkIxAnhZMJXCLaVvHqv9l-AAUV7esZvkQR1IpwBAiDQJh4qxPjFGylyXrHMqh5NlT_pWL2ZoULWTg_TJjMO9TuQ",
                new JwtAuthenticationToken("Random id", "Random username", Role.ADMIN));
    }


}
