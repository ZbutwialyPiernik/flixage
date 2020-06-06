package com.zbutwialypiernik.flixage.service.resource;

import com.zbutwialypiernik.flixage.entity.file.FileEntity;
import com.zbutwialypiernik.flixage.exception.ResourceLoadingException;
import com.zbutwialypiernik.flixage.exception.UnsupportedMediaTypeException;
import lombok.extern.log4j.Log4j2;
import org.springframework.content.commons.repository.ContentStore;
import com.zbutwialypiernik.flixage.util.ExtensionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 *
 * @param <E>
 * @param <T>
 */
@Log4j2
public abstract class AbstractResourceService<E extends FileEntity, T extends AbstractResource> {

    protected final ContentStore<E, String> store;

    public AbstractResourceService(ContentStore<E, String> store) {
        this.store = store;
    }

    public void save(E entity, T resource) {
        final String originalExtension = resource.getExtension();

        if (resource.getAcceptedExtensions().stream()
                .noneMatch((type) -> type.equals(originalExtension))) {
            throw new UnsupportedMediaTypeException("Unsupported file extension: " + resource.getExtension());
        }

        resource = processFile(resource);

        try (InputStream inputStream = resource.getInputStream()) {
            entity.setExtension(resource.getExtension());
            entity.setMimeType(ExtensionUtils.getMimeType(resource.getExtension()));
            store.setContent(entity, inputStream);

            saveMetadata(entity, resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(E entity) {
        store.unsetContent(entity);
    }

    public Optional<T> get(E entity) {
        if (entity == null) {
            return Optional.empty();
        }

        try (var inputStream = store.getContent(entity)) {
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
     *
     * @param entity
     */
    protected abstract void saveMetadata(E entity, T resource);

    /**
     *
     */
    protected abstract T createResource(InputStream inputStream, E entity) throws IOException;

}
