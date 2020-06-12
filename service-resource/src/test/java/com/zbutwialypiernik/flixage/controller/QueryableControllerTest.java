package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.TestWithPrincipal;
import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.service.QueryableService;
import io.restassured.http.ContentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = PlaylistController.class)
public class QueryableControllerTest extends TestWithPrincipal {

    @EqualsAndHashCode(callSuper = true)
    @Data
    static class QueryableStub extends Queryable {
        private String testField;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    static class QueryableResponseStub extends QueryableResponse {
        private String testField;
    }

    QueryableController<QueryableStub, QueryableResponseStub> controller = new ;

    QueryableService<QueryableStub> service;

    @BeforeEach
    public void setup() {
        mockMvc(mockMvc);
    }

    public void returns_entity_when_exists() {
        final var entityId = "0000-0000-0000-0000";

        QueryableStub stub = new QueryableStub();
        stub.setId(entityId);
        stub.setName("My Old Playlist Name");

        when(service.findById(Mockito.any())).thenReturn(Optional.of(stub));

        given().header("Authorization", "Bearer token")
                .contentType(ContentType.JSON)
                .when()
                .delete("/playlists/" + entityId)
                .then()
                .status(HttpStatus.FORBIDDEN);
    }

}
