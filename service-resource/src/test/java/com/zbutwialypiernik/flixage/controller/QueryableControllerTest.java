package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.TestWithPrincipal;
import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.service.QueryableService;
import io.restassured.http.ContentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.glasnost.orika.MapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(QueryableControllerTest.StubController.class)
public class QueryableControllerTest extends TestWithPrincipal {

    @Autowired
    public MockMvc mockMvc;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class QueryableStub extends Queryable {
        private String testField;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class QueryableResponseStub extends QueryableResponse {
        private String testField;
    }

    @RestController
    @RequestMapping("/stubs")
    public static class StubController extends QueryableController<QueryableStub, QueryableResponseStub> {

        public StubController(QueryableService<QueryableStub> service, MapperFactory mapperFactory) {
            super(service, mapperFactory);

            System.out.println("JESTEM ELUWINA");
        }

    }

    @MockBean
    public QueryableService<QueryableStub> service;

    @BeforeEach
    public void setup() {
        mockMvc(mockMvc);
        System.out.println("XDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDQWerqwerqwerqwe");
        mockMvc.getDispatcherServlet().getWebApplicationContext()
                .getBeansWithAnnotation(RestController.class)
                .forEach((xd, xdd) -> {
                    System.out.println(xd);
                    System.out.println(xdd);
                });
    }

    @Test
    public void returns_entity_when_exists() {
        final var entityId = "0000-0000-0000-0000";

        QueryableStub stub = new QueryableStub();
        stub.setId(entityId);
        stub.setName("My Old Playlist Name");

        when(service.findById(Mockito.any())).thenReturn(Optional.of(stub));

        given().header("Authorization", "Bearer token")
                .contentType(ContentType.JSON)
                .when()
                .get("/stubs/" + entityId)
                .then()
                .status(HttpStatus.OK);
    }

}
