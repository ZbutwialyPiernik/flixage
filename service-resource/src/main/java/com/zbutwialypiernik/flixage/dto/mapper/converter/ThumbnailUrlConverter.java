package com.zbutwialypiernik.flixage.dto.mapper.converter;

import com.zbutwialypiernik.flixage.config.GatewayUriFactory;
import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.entity.Queryable;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

/**
 * Custom Orika mapper implementation to map {@link Queryable#getThumbnail()} ()} to {@link QueryableResponse#getThumbnailUrl()}.
 * For simplicity CDN server will be not used, static content will be served from REST API.
 */
public class ThumbnailUrlConverter<A extends Queryable, B extends QueryableResponse> extends CustomMapper<A, B> {

    private final GatewayUriFactory builder;
    private final String resourcePath;

    /**
     * @param resourcePath the api path to resource example: host.com/api/v1/{resourcePath}/1324-1234-1234-1234/thumbnail
     */
    public ThumbnailUrlConverter(GatewayUriFactory builder, String resourcePath) {
        this.builder = builder;
        this.resourcePath = resourcePath;
    }

    @Override
    public void mapAtoB(A queryable, B queryableResponse, MappingContext context) {
        super.mapAtoB(queryable, queryableResponse, context);

        if (queryable.getThumbnail() == null) {
            return;
        }

        queryableResponse.setThumbnailUrl(builder.createUriBuilder()
                .pathSegment(resourcePath, queryable.getId(), "thumbnail")
                .build()
                .toUriString());
    }

    @Override
    public void mapBtoA(B queryableResponse, A queryable, MappingContext context) {
        throw new IllegalStateException("This class is response, not request");
    }

}
