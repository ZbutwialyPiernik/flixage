package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.entity.Thumbnail;
import com.zbutwialypiernik.flixage.exception.*;
import com.zbutwialypiernik.flixage.repository.QueryableRepository;
import com.zbutwialypiernik.flixage.service.file.resource.ImageResource;
import com.zbutwialypiernik.flixage.service.file.ThumbnailFileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Base class for every service in project, contains common crud methods
 */
@Log4j2
public class QueryableService<T extends Queryable>{

    protected final QueryableRepository<T> repository;

    protected final ThumbnailFileService thumbnailService;

    private final Clock clock;

    public QueryableService(QueryableRepository<T> repository, ThumbnailFileService thumbnailService, Clock clock) {
        this.thumbnailService = thumbnailService;
        this.clock = clock;
        this.repository = repository;
    }

    public Optional<T> findById(String id) {
        return repository.findById(id);
    }

    /**
     * Creates new entity
     * @throws BadRequestException when entity has set explicit id
     * @throws IllegalArgumentException when thumbnail is explicitly set
     * @param entity the entity without id and thumbnail
     */
    public T create(T entity) {
        if (entity.getId() != null) {
            throw new BadRequestException("User id should not be set");
        }

        if (entity.getThumbnail() != null) {
            throw new IllegalArgumentException("Thumbnail cannot be explicitly set during entity creation");
        }

        entity.setCreationTime(getCurrentInstant());
        entity.setLastUpdateTime(entity.getCreationTime());

        return repository.save(entity);
    }

    /**
     * Updates existing entity
     * @throws ResourceNotFoundException when entity doesn't exists in database
     * @throws IllegalStateException when creation date is other than existing in database
     *
     * @param entity
     */
    @Transactional
    public T update(T entity) {
        T oldEntity = findById(entity.getId()).orElseThrow(ResourceNotFoundException::new);

        // Thumbnail cannot be changed without calling saveThumbnail(),
        // because is tied with file, without that we would have oprhan files
        entity.setThumbnail(oldEntity.getThumbnail());
        entity.setLastUpdateTime(getCurrentInstant());
        entity.setCreationTime(oldEntity.getCreationTime());

        return repository.save(entity);
    }

    /**
     * Deletes entity by given id
     * @throws ResourceNotFoundException when entity does not exists in database
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
     * @throws ResourceNotFoundException when entity does not exists in database
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
     * @param name the query to find similar entities by name
     * @param offset the first index of page
     * @param limit the limit of entities per page
     * @return the page of entities containing query string in their name
     *
     * @throws BadRequestException when offset is negative
     * @throws BadRequestException when limit is non positive
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
     * @param entity
     * @param resource
     * @return
     */
    @Transactional
    public T saveThumbnail(T entity, ImageResource resource) {
        if (entity.getThumbnail() == null) {
            entity.setThumbnail(new Thumbnail());
        }

        thumbnailService.save(entity.getThumbnail(), resource);

        return update(entity);
    }

    /**
     * Deletes thumbnail associated with entity
     * @param entity
     */
    @Transactional
    public void deleteThumbnail(T entity) {
        if (entity.getThumbnail() == null) {
            return;
        }

        thumbnailService.delete(entity.getThumbnail());
        entity.setThumbnail(null);

        update(entity);
    }

    /**
     * Returns thumbnail associated with entity by id
     * @param id the id of entity
     * @return the optional of byte[] containing thumbnail
     */
    public Optional<ImageResource> getThumbnailById(String id) {
        T t = findById(id).orElseThrow(ResourceNotFoundException::new);

        return thumbnailService.get(t.getThumbnail());
    }

    /**
     * Database does not store nano seconds, we're cutting them to have same precision
     * in database and entities at same time.
     * @return current UTC
     */
    private Instant getCurrentInstant() {
        return clock.instant().truncatedTo(ChronoUnit.MILLIS);
    }

}
