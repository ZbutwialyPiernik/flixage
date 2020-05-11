package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.BaseEntity;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Base class for every service in project, contains common crud methods
 * Every {@link BaseEntity} uses uuid as ID, so we define explicitly String as expected type
 * @param <T> type
 */
public class CrudService<T extends BaseEntity> {

    private final CrudRepository<T, String> repository;

    private final Clock clock;

    @Autowired
    public CrudService(CrudRepository<T, String> repository, Clock clock) {
        this.clock = clock;
        this.repository = repository;
    }

    public T findById(String id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * Creates new entity
     * @throws BadRequestException when entity has set explicit id
     *
     * @param entity
     */
    public T create(T entity) {
        if (entity.getId() != null) {
            throw new BadRequestException("User id should not be set");
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
        T oldEntity = findById(entity.getId());

        System.out.println(oldEntity);
        System.out.println(entity);

        if (!oldEntity.getCreationTime().equals(entity.getCreationTime())) {
            throw new IllegalStateException("Creation date is other than existing in database");
        }

        entity.setLastUpdateTime(getCurrentInstant());

        return repository.save(entity);
    }

    /**
     * Deletes entity by given id
     * @throws ResourceNotFoundException when entity does not exists
     * @param id id of entity to be deleted
     */
    public void deleteById(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException();
        }

        repository.deleteById(id);
    }

    /**
     * Deletes given entity
     * @throws ResourceNotFoundException when entity does not exists
     * @param entity entity to be deleted
     */
    public void delete(T entity) {
        this.deleteById(entity.getId());
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
