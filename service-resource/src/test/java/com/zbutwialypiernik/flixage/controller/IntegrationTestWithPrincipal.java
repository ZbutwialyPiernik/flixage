package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.AuthenticationTestHelper;
import com.zbutwialypiernik.flixage.entity.Role;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationParser;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import com.zbutwialypiernik.flixage.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static com.zbutwialypiernik.flixage.filter.JwtAuthenticationFilter.TOKEN_PREFIX;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.mockito.Mockito.when;

// Recreates database after every test
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class IntegrationTestWithPrincipal {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    public JwtAuthenticationParser parser;

    @Autowired
    public UserService userService;

    public User user;

    public User otherUser;

    public JwtAuthenticationToken token;

    public static final String TOKEN = "token";

    public static final String TOKEN_HEADER = TOKEN_PREFIX + TOKEN;

    @BeforeEach
    void prepare() {
        mockMvc(mockMvc);

        user = new User();
        user.setUsername("user");
        user.setPassword("Passw0rd");
        user.setRole(Role.USER);

        otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setPassword("Passw0rd");
        otherUser.setRole(Role.USER);

        userService.create(user);
        userService.create(otherUser);

        token = AuthenticationTestHelper.createMockAuthentication(user);

        when(parser.parseToken(TOKEN)).thenReturn(token);
    }

}
