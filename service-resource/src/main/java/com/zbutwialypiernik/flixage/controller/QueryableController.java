package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.exception.ResourceLoadingException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;
import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;


/**
 * Base controller for entities extending {@link Queryable}, contains only read-only methods
 * @param <E> Entity that extends {@link Queryable}
 * @param <DTO> DTO representing response of {@link Queryable} entity
 */
@Log4j2
public class QueryableController<E extends Queryable, DTO extends QueryableResponse> {

    private final QueryableService<E> service;
    protected final BoundMapperFacade<E, DTO> dtoMapper;

    @Autowired
    public QueryableController(QueryableService<E> service, MapperFactory mapperFactory) {
        this.service = service;

        Type<QueryableController<E, DTO>> entityType = new TypeBuilder<QueryableController<E, DTO>>() {}.build();

        this.dtoMapper = mapperFactory.getMapperFacade(entityType.getNestedType(0), entityType.getNestedType(1));
    }

    @GetMapping("/{id}")
    public DTO getById(@PathVariable String id) {
        return dtoMapper.map(service.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    @GetMapping(value = "/{id}/thumbnail", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getThumbnail(@PathVariable String id) {
        try {
            return service.getThumbnailById(id).map(ImageResource::getInputStream).orElseThrow(() -> new ResourceNotFoundException("Image not found")).readAllBytes();
        } catch (IOException e) {
            log.error("Error during loading of thumbnail", e);
            throw new ResourceLoadingException("Error during loading of thumbnail");
        }
    }

}
