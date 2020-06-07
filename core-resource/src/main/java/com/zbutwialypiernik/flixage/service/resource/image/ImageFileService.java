package com.zbutwialypiernik.flixage.service.resource.image;

import com.zbutwialypiernik.flixage.entity.file.ImageFileEntity;
import com.zbutwialypiernik.flixage.repository.ImageFileStore;
import com.zbutwialypiernik.flixage.service.resource.AbstractResourceService;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;

@Log4j2
public class ImageFileService extends AbstractResourceService<ImageFileEntity, ImageResource> {

    private final ImageProcessingService processingService;

    public ImageFileService(ImageFileStore store, ImageProcessingService processingService) {
        super(store, ImageResource.ACCEPTED_EXTENSIONS);
        this.processingService = processingService;
    }

    @Override
    public ImageResource processFile(ImageResource resource) {
        return processingService.process(resource);
    }

    @Override
    public void saveMetadata(ImageFileEntity entity, ImageResource resource) {
        entity.setWidth(resource.getWidth());
        entity.setHeight(resource.getHeight());
    }

    @Override
    protected ImageResource createResource(InputStream inputStream, ImageFileEntity entity) throws IOException {
        return new ImageResource(inputStream.readAllBytes(), entity.getFileId(), entity.getExtension(), entity.getMimeType());
    }

}
