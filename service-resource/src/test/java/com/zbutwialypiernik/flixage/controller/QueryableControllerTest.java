package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.service.QueryableService;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class QueryableControllerTest {

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

    QueryableController<QueryableStub, QueryableResponseStub> controller;

    QueryableService<QueryableStub> service;

}
