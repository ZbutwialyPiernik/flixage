package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.PageResponse;
import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.service.QueryableService;
import com.zbutwialypiernik.flixage.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Base controller for entities extending {@link Queryable}, contains only read-only methods
 * @param <T> Entity that extends {@link Queryable}
 * @param <DTO> DTO representing response of {@link Queryable} entity
 */
public abstract class QueryableController<T extends Queryable, DTO extends QueryableResponse> {

    private final QueryableService<T> service;

    @Autowired
    public QueryableController(QueryableService<T> service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<T> findByName(@RequestParam String query, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {
        if (StringUtils.isBlank(query)) {
            throw new BadRequestException("Param 'query' cannot be blank");
        }

        Page<T> page = service.findByName(query, offset, limit);;

        return new PageResponse<>(page.getContent(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    public DTO getById(String id) {
        return toResponse(service.findById(id));
    }

    @GetMapping("/{id}/thumbnail")
    public byte[] getThumbnail(String id) {
        return service.getThumbnailById(id);
    }

    public abstract DTO toResponse(T t);

}
