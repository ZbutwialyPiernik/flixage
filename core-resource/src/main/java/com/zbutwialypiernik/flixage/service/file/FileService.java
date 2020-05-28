package com.zbutwialypiernik.flixage.service.file;

import com.zbutwialypiernik.flixage.service.file.resource.AbstractResource;

import java.util.Optional;

public interface FileService<T, R extends AbstractResource> {

    void save(T entity, R resource);

    void delete(T entity);

    Optional<R> get(T entity);

}
