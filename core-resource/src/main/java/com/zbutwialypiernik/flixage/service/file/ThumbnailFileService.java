package com.zbutwialypiernik.flixage.service.file;

import com.zbutwialypiernik.flixage.entity.Thumbnail;
import com.zbutwialypiernik.flixage.exception.ResourceLoadingException;
import com.zbutwialypiernik.flixage.exception.UnsupportedMediaTypeException;
import com.zbutwialypiernik.flixage.repository.ThumbnailFileStore;
import com.zbutwialypiernik.flixage.service.ImageProcessingService;
import com.zbutwialypiernik.flixage.service.file.resource.ImageResource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import util.ExtensionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

@Log4j2
@Service
public class ThumbnailFileService {

    private final ThumbnailFileStore store;
    private final ImageProcessingService imageService;

    public ThumbnailFileService(ThumbnailFileStore store, ImageProcessingService imageService) {
        this.store = store;
        this.imageService = imageService;
    }

    public void save(Thumbnail thumbnail, ImageResource resource) {
        final String originalExtension = resource.getExtension();

        if (Arrays.stream(ImageResource.ACCEPTED_EXTENSIONS)
                .noneMatch((type) -> type.equals(originalExtension))) {
            throw new UnsupportedMediaTypeException("Unsupported file extension: " + resource.getExtension());
        }

        resource = imageService.process(resource);

        try (InputStream inputStream = resource.getInputStream()){
            thumbnail.setExtension(originalExtension);
            thumbnail.setMimeType(ExtensionUtils.getMimeType(originalExtension));
            store.setContent(thumbnail, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(Thumbnail thumbnail) {
        store.unsetContent(thumbnail);
    }

    public Optional<ImageResource> get(Thumbnail thumbnail) {
        if (thumbnail == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(new ImageResource(store.getContent(thumbnail).readAllBytes(), thumbnail.getFileId(), thumbnail.getExtension(), thumbnail.getMimeType()));
        } catch (IOException e) {
            log.error("Problem during loading of thumbnail: ", e);
            throw new ResourceLoadingException("Problem during loading of thumbnail");
        }
    }

}
