package com.zbutwialypiernik.flixage.dto.mapper;

import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.entity.file.ImageFileEntity;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

public class QueryableResponseMapperTest {

    // Test field is made only to test if Orika will map child fields too.

    public static class QueryableEntityTest extends Queryable {

        private String testField;

        public String getTestField() {
            return testField;
        }

        public void setTestField(String testField) {
            this.testField = testField;
        }
    }

    public static class QueryableResponseTest extends QueryableResponse {

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
    public void entity_gets_mapped_properly_when_thumbnail_is_not_null() {
        final var entity = new QueryableEntityTest();
        entity.setId("1234-1234-1234-1234");
        entity.setName("Test name");
        entity.setTestField("Test field");
        entity.setThumbnail(new ImageFileEntity());

        var mapperFactory = new DefaultMapperFactory.Builder()
                .build();

        DtoMappersConfiguration mappersConfiguration = new DtoMappersConfiguration(mapperFactory, () -> UriComponentsBuilder.fromUriString(BASE_URL));
        mappersConfiguration.createCustomMapping(QueryableEntityTest.class, QueryableResponseTest.class, "stubs").register();

        MapperFacade mapperFacade = mapperFactory.getMapperFacade();
        QueryableResponseTest response = mapperFacade.map(entity, QueryableResponseTest.class);

        Assertions.assertEquals(entity.getId(), response.getId());
        Assertions.assertEquals(entity.getName(), response.getName());
        Assertions.assertEquals(entity.getTestField(), response.getTestField());
        Assertions.assertEquals(BASE_URL + "/stubs/" + entity.getId() + "/thumbnail", response.getThumbnailUrl());
    }

    @Test
    public void entity_gets_mapped_properly_when_thumbnail_is_null() {
        var entityStub = new QueryableEntityTest();
        entityStub.setId("1234-1234-1234-1234");
        entityStub.setName("Test name");
        entityStub.setTestField("Test field");
        entityStub.setThumbnail(null);

        var mapperFactory = new DefaultMapperFactory.Builder()
                .build();

        DtoMappersConfiguration mappersConfiguration = new DtoMappersConfiguration(mapperFactory, () -> UriComponentsBuilder.fromUriString(BASE_URL));
        mappersConfiguration.createCustomMapping(QueryableEntityTest.class, QueryableResponseTest.class, "stubs");

        MapperFacade mapperFacade = mapperFactory.getMapperFacade();
        QueryableResponseTest responseStub = mapperFacade.map(entityStub, QueryableResponseTest.class);

        Assertions.assertEquals(entityStub.getId(), responseStub.getId());
        Assertions.assertEquals(entityStub.getName(), responseStub.getName());
        Assertions.assertEquals(entityStub.getTestField(), responseStub.getTestField());
        Assertions.assertNull(responseStub.getThumbnailUrl());
    }

}
