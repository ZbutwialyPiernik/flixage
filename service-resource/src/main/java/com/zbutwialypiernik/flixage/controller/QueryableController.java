package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.PageResponse;
import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.util.StringUtils;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;


/**
 * Base controller for entities extending {@link Queryable}, contains only read-only methods
 * @param <E> Entity that extends {@link Queryable}
 * @param <DTO> DTO representing response of {@link Queryable} entity
 */
public class QueryableController<E extends Queryable, DTO extends QueryableResponse> {

    private final QueryableService<E> service;
    protected final BoundMapperFacade<E, DTO> dtoMapper;

    @Autowired
    public QueryableController(QueryableService<E> service, MapperFactory mapperFactory) {
        this.service = service;

        Type<QueryableController<E, DTO>> entityType = new TypeBuilder<QueryableController<E, DTO>>() {}.build();

        this.dtoMapper = mapperFactory.getMapperFacade(entityType.getNestedType(0), entityType.getNestedType(1));
    }

    @GetMapping
    public PageResponse<DTO> findByName(@RequestParam String query, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {
        if (StringUtils.isBlank(query)) {
            throw new BadRequestException("Param 'query' cannot be blank");
        }

        Page<E> page = service.findByName(query, offset, limit);;

        return new PageResponse<>(page.getContent()
                .stream()
                .map(dtoMapper::map)
                .collect(Collectors.toList()), page.getTotalElements());
    }

    @GetMapping("/{id}")
    public DTO getById(@PathVariable String id) {
        return dtoMapper.map(service.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    @GetMapping(value = "/{id}/thumbnail", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getThumbnail(@PathVariable String id) {
        return service.getThumbnailById(id).orElseThrow(() -> new ResourceNotFoundException("Image not found"));
    }

}
