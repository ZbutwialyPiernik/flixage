package com.zbutwialypiernik.flixage.dto.mapper;

import com.zbutwialypiernik.flixage.config.GatewayUriBuilder;
import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.entity.file.ImageFileEntity;
import ma.glasnost.orika.MapperFacade;
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
        entityStub.setThumbnail(new ImageFileEntity());

        var mapperFactory = new DefaultMapperFactory.Builder()
                .build();

        DtoMappersConfiguration mappersConfiguration = new DtoMappersConfiguration(mapperFactory, new GatewayUriBuilder(BASE_URL));
        mappersConfiguration.createCustomMapping(QueryableStub.class, QueryableResponseStub.class, "stubs");

        MapperFacade mapperFacade = mapperFactory.getMapperFacade();
        QueryableResponseStub responseStub = mapperFacade.map(entityStub, QueryableResponseStub.class);

        Assertions.assertEquals(entityStub.getId(), responseStub.getId());
        Assertions.assertEquals(entityStub.getName(), responseStub.getName());
        Assertions.assertEquals(entityStub.getTestField(), responseStub.getTestField());
        Assertions.assertEquals(BASE_URL + "/stubs/" + entityStub.getId() + "/thumbnail", responseStub.getThumbnailUrl());
    }

}
