package com.zbutwialypiernik.flixage;

import com.zbutwialypiernik.flixage.config.GatewayUriBuilder;
import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.dto.mapper.MappersConfiguration;
import com.zbutwialypiernik.flixage.dto.mapper.converter.ThumbnailUrlConverter;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.entity.Thumbnail;
import ma.glasnost.orika.*;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueryableResponseMapperTest {

    // Test field is made only to test if Orika will map child fields too.

    public static class QueryableStub extends Queryable {

        private String testField;

        public String getTestField() {
            return testField;
        }

        public void setTestField(String testField) {
            this.testField = testField;
        }
    }

    public static class QueryableResponseStub extends QueryableResponse {

        private String testField;

        public String getTestField() {
            return testField;
        }

        public void setTestField(String testField) {
            this.testField = testField;
        }
    }

    private final static String BASE_URL = "http://host.com/api/v1";

    @Test
    public void entity_gets_mapped_properly() {
        var entityStub = new QueryableStub();
        entityStub.setId("1234-1234-1234-1234");
        entityStub.setName("Test name");
        entityStub.setTestField("Test field");
        entityStub.setThumbnail(new Thumbnail());

        var mapperFactory = new DefaultMapperFactory.Builder()
                .build();

        MappersConfiguration mappersConfiguration = new MappersConfiguration(mapperFactory, new GatewayUriBuilder(BASE_URL));
        mappersConfiguration.createCustomMapping(QueryableStub.class, QueryableResponseStub.class, "stubs");

        MapperFacade mapperFacade = mapperFactory.getMapperFacade();
        QueryableResponseStub responseStub = mapperFacade.map(entityStub, QueryableResponseStub.class);

        Assertions.assertEquals(entityStub.getId(), responseStub.getId());
        Assertions.assertEquals(entityStub.getName(), responseStub.getName());
        Assertions.assertEquals(entityStub.getTestField(), responseStub.getTestField());
        Assertions.assertEquals(BASE_URL + "/stubs/" + entityStub.getId() + "/thumbnail", responseStub.getThumbnailUrl());
    }

}
