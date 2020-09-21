package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.service.DatabaseUserDetails;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class RegisterControllerIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private DatabaseUserDetails userDetails;

    @BeforeEach
    void prepare() {
        mockMvc(mockMvc);
    }

    @Test
    void user_is_able_to_register_when_username_is_not_taken() {
        final var username = "newuser123";
        final var registerRequest = new RegisterController.RegisterRequest(username, "Passw0rd");

        // @formatter:off
        given()
            .body(registerRequest)
            .contentType(ContentType.JSON)
        .when()
            .post("/authentication/register")
        .then()
            .status(HttpStatus.CREATED)
            .body("refreshToken", notNullValue())
            .body("accessToken", notNullValue())
            .body("expireTime", notNullValue());
        // @formatter:on

        assertNotNull(userDetails.loadUserByUsername(username));
    }

    @Test
    void user_is_not_able_to_register_when_username_is_taken() {
        final var username = "newuser123";

        final var user = new User();
        user.setUsername(username);
        user.setPassword("Xddddddd123");

        userDetails.registerUser(user);

        final var registerRequest = new RegisterController.RegisterRequest(username, "Passw0rd");

        // @formatter:off
        given()
            .body(registerRequest)
            .contentType(ContentType.JSON)
        .when()
            .post("/authentication/register")
        .then()
            .status(HttpStatus.CONFLICT);
        // @formatter:on
    }

    @Test
    void user_is_not_able_to_register_when_username_is_null() {
        final var registerRequest = new RegisterController.RegisterRequest(null, "Passw0rd");

        // @formatter:off
        given()
                .body(registerRequest)
                .contentType(ContentType.JSON)
                .when()
                .post("/authentication/register")
                .then()
                .status(HttpStatus.BAD_REQUEST);
        // @formatter:on
    }

    @Test
    void user_is_not_able_to_register_when_password_is_null() {
        final var username = "newuser123";
        final var registerRequest = new RegisterController.RegisterRequest(username, null);

        // @formatter:off
        given()
                .body(registerRequest)
                .contentType(ContentType.JSON)
                .when()
                .post("/authentication/register")
                .then()
                .status(HttpStatus.BAD_REQUEST);
        // @formatter:on
    }

}
