package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.entity.file.ImageFileEntity;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.QueryableRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Base class for every service with queryable superclass in project
 * contains common crud methods and methods to set/delete thumbnail of entity
 */
@Log4j2
public class QueryableService<T extends Queryable>{

    private final QueryableRepository<T> repository;

    private final ImageFileService thumbnailService;

    public QueryableService(QueryableRepository<T> repository, ImageFileService thumbnailService) {
        this.thumbnailService = thumbnailService;
        this.repository = repository;
    }

    public Optional<T> findById(String id) {
        return repository.findById(id);
    }

    /**
     * Creates new entity
     *
     * @throws IllegalArgumentException when entity has set explicit thumbnail
     *
     * @param entity the entity without thumbnail
     */
    public T create(T entity) {
        if (entity.getThumbnail() != null) {
            throw new IllegalArgumentException("Thumbnail cannot be explicitly set during entity creation");
        }

        entity.setId(UUID.randomUUID().toString());

        return repository.save(entity);
    }

    /**
     * Updates existing entity
     *
     * @throws ResourceNotFoundException when entity doesn't exists in database
     *
     * @param entity the entity to update
     * @return the updated entity
     */
    @Transactional
    public T update(T entity) {
        if (!repository.existsById(entity.getId())) {
            throw new ResourceNotFoundException();
        }

        return repository.save(entity);
    }

    /**
     * Deletes entity by given id
     *
     * @throws ResourceNotFoundException when entity does not exists in database
     *
     * @param id id of entity to be deleted
     */
    @Transactional
    public void deleteById(String id) {
        T entity = findById(id).orElseThrow(ResourceNotFoundException::new);

        if (entity.getThumbnail() != null) {
            thumbnailService.delete(entity.getThumbnail());
        }

        repository.deleteById(id);
    }

    /**
     * Deletes given entity
     *
     * @throws ResourceNotFoundException when entity does not exists in database
     *
     * @param entity entity to be deleted
     */
    @Transactional
    public void delete(T entity) {
        // Entity can be detached
        if (!repository.existsById(entity.getId())) {
            throw new ResourceNotFoundException();
        }

        if (entity.getThumbnail() != null) {
            thumbnailService.delete(entity.getThumbnail());
        }

        repository.deleteById(entity.getId());
    }

    /**
     * Finds page of entities with username containing provided query, ignoring case.
     *
     * @throws BadRequestException when offset is negative
     * @throws BadRequestException when limit is non positive
     *
     * @param name the query to find similar entities by name
     * @param offset the first index of page
     * @param limit the limit of entities per page
     *
     * @return the page of entities containing query string in their name
     */
    public Page<T> findByName(String name, int offset, int limit) {
        return repository.findByNameContainingIgnoreCase(name, PageRequest.of(offset / limit, limit));
    }

    /**
     * Counts entities with username containing provided query, ignoring case.
     * @param query the query to find similar entities by name
     * @return the count of entities containing query string in their name
     */
    public int countByName(String query) {
        return repository.countByNameContainingIgnoreCase(query);
    }

    /**
     * Associates thumbnail to entity
     * @param entity the entity to associate with thumbnail
     * @param resource the image resource to associate with entity
     */
    @Transactional
    public void saveThumbnail(T entity, ImageResource resource) {
        if (!repository.existsById(entity.getId())) {
            throw new ResourceNotFoundException();
        }

        if (entity.getThumbnail() == null) {
            entity.setThumbnail(new ImageFileEntity());
            entity.getThumbnail().setId(UUID.randomUUID().toString());
        }

        thumbnailService.save(entity.getThumbnail(), resource);

        repository.save(entity);
    }

    /**
     * Deletes thumbnail associated with entity
     * @param entity the entity with thumbnail to delete
     */
    @Transactional
    public void deleteThumbnail(T entity) {
        if (entity.getThumbnail() == null) {
            return;
        }

        thumbnailService.delete(entity.getThumbnail());
        entity.setThumbnail(null);

        repository.save(entity);
    }

    /**
     * Returns thumbnail associated with entity by id
     * @param id the id of entity
     * @return the optional of byte[] containing thumbnail
     */
    public Optional<ImageResource> getThumbnailById(String id) {
        T entity = findById(id).orElseThrow(ResourceNotFoundException::new);

        return thumbnailService.get(entity.getThumbnail());
    }

    protected QueryableRepository<T> getRepository() {
        return repository;
    }
}
