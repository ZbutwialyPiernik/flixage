package com.zbutwialypiernik.flixage.dto.mapper.converter;

import com.zbutwialypiernik.flixage.config.GatewayUriBuilder;
import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.entity.Queryable;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

/**
 * Custom Orika mapper implementation to map {@link Queryable#getThumbnail()} to {@link QueryableResponse#getThumbnailUrl()}.
 * For simplicity CDN server will be not used, static content will be served from REST API.
 */
public class ThumbnailUrlConverter<A extends Queryable, B extends QueryableResponse> extends CustomMapper<A, B> {

    private final GatewayUriBuilder builder;
    private final String resourcePath;

    /**
     * @param resourcePath the api path to resource example: gateway.com/api/v1/{resourcePath}/1324-1234-1234-1234/thumbnail
     */
    public ThumbnailUrlConverter(GatewayUriBuilder builder, String resourcePath) {
        this.builder = builder;
        this.resourcePath = resourcePath;
    }

    @Override
    public void mapAtoB(A queryable, B queryableResponse, MappingContext context) {
        super.mapAtoB(queryable, queryableResponse, context);

        queryableResponse.setThumbnailUrl(builder.newBuilder()
                .pathSegment(resourcePath, queryable.getId(), "thumbnail")
                .build()
                .toUriString());
    }

    @Override
    public void mapBtoA(B queryableResponse, A queryable, MappingContext context) {
        super.mapBtoA(queryableResponse, queryable, context);

        throw new IllegalStateException("This class is response, not request");
    }

}
