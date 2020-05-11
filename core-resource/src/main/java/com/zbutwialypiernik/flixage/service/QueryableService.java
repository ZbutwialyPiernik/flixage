package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Queryable;
import com.zbutwialypiernik.flixage.entity.Thumbnail;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.exception.ResourceLoadingException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.QueryableRepository;
import com.zbutwialypiernik.flixage.repository.ThumbnailStore;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Clock;

@Log4j2
public class QueryableService<T extends Queryable> extends CrudService<T> {

    protected final QueryableRepository<T> repository;
    protected final ThumbnailStore store;

    public static final String[] ACCEPTED_EXTENSIONS = {"jpg", "jpeg", "png"};
    public static final String[] ACCEPTED_MIME_TYPES =  {"images/jpeg", "png"};

    private static final String OUTPUT_FORMAT = "png";

    public QueryableService(QueryableRepository<T> repository, ThumbnailStore store, Clock clock) {
        super(repository, clock);
        this.repository = repository;
        this.store = store;
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

    public void saveThumbnail(T t, File file) {
        transcodeToOutputFormat(file);

        if (t.getThumbnail() == null) {
            t.setThumbnail(new Thumbnail());
        }

        try (InputStream inputStream = new FileInputStream(file)){
            store.setContent(t.getThumbnail(), inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.update(t);
    }

    public void deleteThumbnail(T t) {
        store.unsetContent(t.getThumbnail());

        super.update(t);
    }

    public byte[] getThumbnailById(String id) {
        T t = findById(id);

        if (t.getThumbnail() == null) {
            throw new ResourceNotFoundException();
        }

        Resource resource = store.getResource(t.getThumbnail());

        if (!resource.exists()) {
            throw new ResourceNotFoundException();
        }

        try (InputStream stream = store.getContent(t.getThumbnail())) {
            return stream.readAllBytes();
        } catch (IOException e) {
            log.error("Error during loading thumbnail: ", e);
            throw new ResourceLoadingException("Unexpected error during loading resource");
        }
    }

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    // TODO: 100% refactor in future
    private void transcodeToOutputFormat(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)){
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ImageIO.write(bufferedImage, OUTPUT_FORMAT, new FileOutputStream(file));
        } catch (IOException e) {
            throw new RuntimeException("Problem while transcoding image");
        }
    }
    
}
