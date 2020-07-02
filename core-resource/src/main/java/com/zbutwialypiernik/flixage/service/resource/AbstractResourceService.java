package com.zbutwialypiernik.flixage.service.resource;

import com.zbutwialypiernik.flixage.entity.file.FileEntity;
import com.zbutwialypiernik.flixage.exception.ResourceLoadingException;
import com.zbutwialypiernik.flixage.exception.UnsupportedMediaTypeException;
import com.zbutwialypiernik.flixage.util.ExtensionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.content.commons.repository.ContentStore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

/**
 * The abstract resource service that filters entities by mime-type
 * and allows to process resource before saving to database
 * @param <E> the entity representing resource
 * @param <T> the resource associated with entity
 */
@Log4j2
public abstract class AbstractResourceService<E extends FileEntity, T extends AbstractResource> {

    protected final ContentStore<E, String> store;

    private final Set<String> acceptedExtension;

    public AbstractResourceService(ContentStore<E, String> store, Set<String> acceptedExtension) {
        this.store = store;
        this.acceptedExtension = acceptedExtension;
    }

    public void save(E entity, T resource) {
        final String originalExtension = resource.getExtension();

        if (acceptedExtension.stream()
                .noneMatch(type -> type.equals(originalExtension))) {
            throw new UnsupportedMediaTypeException("Unsupported file extension: " + resource.getExtension());
        }

        resource = processFile(resource);

        try (InputStream inputStream = resource.getInputStream()) {
            entity.setExtension(resource.getExtension());
            entity.setMimeType(ExtensionUtils.getMimeType(resource.getExtension()));
            store.setContent(entity, inputStream);

            saveMetadata(entity, resource);
        } catch (IOException e) {
            log.error("Error during save of resource: ", e);
        }
    }

    public void delete(E entity) {
        store.unsetContent(entity);
    }

    /**
     *
     * @param entity the entity
     * @return the optional of resource or empty when resource entity exists but file is not found
     */
    public Optional<T> get(E entity) {
        if (entity == null) {
            return Optional.empty();
        }

        try (var inputStream = store.getContent(entity)) {
            if (inputStream == null) {
                log.error("Resource not found with file id " + entity.getFileId() + "." + entity.getExtension());

                return Optional.empty();
            }

            return Optional.of(createResource(inputStream, entity));
        } catch (IOException e) {
            log.error("Problem during loading of file: ", e);
            throw new ResourceLoadingException("Problem during loading of file");
        }
    }

    /**
     * The method called before saving the resource to the database,
     * the last possibility to modify the resource
     * @param resource the resource to process
     * @return the new processed resource or od unmodified resource
     */
    protected T processFile(T resource) {
        return resource;
    }

    /**
     * The method called after resource has been saved
     * allows to save additional metadata to entities like width or height of image
     * @param entity the entity to associate with resource
     * @param resource the resource that should be associated with entity
     */
    protected abstract void saveMetadata(E entity, T resource);

    /**
     * The method called during
     */
    protected abstract T createResource(InputStream inputStream, E entity) throws IOException;

}
