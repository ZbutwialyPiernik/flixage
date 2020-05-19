package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.entity.Thumbnail;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.exception.ResourceLoadingException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.exception.UnsupportedMediaTypeException;
import com.zbutwialypiernik.flixage.repository.QueryableRepository;
import com.zbutwialypiernik.flixage.repository.ThumbnailFileStore;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import util.ExtensionUtils;

import java.io.*;
import java.time.Clock;
import java.util.Arrays;
import java.util.Optional;

@Log4j2
public class QueryableService<T extends Queryable> extends CrudService<T> {

    protected final QueryableRepository<T> repository;
    protected final ThumbnailFileStore store;
    protected final ImageProcessingService imageService;

    public static final int MAX_FILE_SIZE = 5 * 1048576;

    public QueryableService(QueryableRepository<T> repository, ThumbnailFileStore store, ImageProcessingService imageService, Clock clock) {
        super(repository, clock);
        this.repository = repository;
        this.store = store;
        this.imageService = imageService;
    }

    /**
     * Finds page of entities with username containing provided query, ignoring case.
     *
     * @param name query to find similar entities by name
     * @param offset first index of page
     * @param limit limit of entities per page
     * @return page of users containing query string in their username
     *
     * @throws BadRequestException when offset is negative
     * @throws BadRequestException when limit is non positive
     */
    public Page<T> findByName(String name, int offset, int limit) {
        if (offset < 0) {
            throw new BadRequestException("offset cannot be negative");
        }

        if (limit <= 0) {
            throw new BadRequestException("limit should be positive");
        }

        return repository.findByNameContainingIgnoreCase(name, PageRequest.of(offset / limit, limit));
    }

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    public int countByName(String name) {
        return repository.countByNameContainingIgnoreCase(name);
    }

    public T saveThumbnail(T entity, ImageResource resource) {
        final String originalExtension = resource.getExtension();

        if (Arrays.stream(ExtensionUtils.ACCEPTED_IMAGE_EXTENSIONS)
                .noneMatch((type) -> type.equals(originalExtension))) {
            throw new UnsupportedMediaTypeException("Unsupported file extension: " + resource.getExtension());
        }

        if (entity.getThumbnail() == null) {
            entity.setThumbnail(new Thumbnail());
        }

        resource = imageService.process(resource);

        try (InputStream inputStream = resource.getInputStream()){
            entity.getThumbnail().setMimeType(ExtensionUtils.getMimeType(resource.getExtension()));
            store.setContent(entity.getThumbnail(), inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.update(entity);
    }

    public void deleteThumbnail(T entity) {
        super.update(entity);
    }

    public Optional<byte[]> getThumbnailById(String id) {
        T t = findById(id).orElseThrow(ResourceNotFoundException::new);

        if (t.getThumbnail() == null) {
            return Optional.empty();
        }

        Resource resource = store.getResource(t.getThumbnail());

        if (resource == null || !resource.exists()) {
            return Optional.empty();
        }

        try (InputStream stream = store.getContent(t.getThumbnail())) {
            return Optional.of(stream.readAllBytes());
        } catch (IOException e) {
            log.error("Error during loading thumbnail: ", e);
            throw new ResourceLoadingException("Unexpected error during loading resource");
        }
    }

    public void deleteAll() {
        repository.deleteAll();
    }


}
