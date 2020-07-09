package com.zbutwialypiernik.flixage.config;

import com.zbutwialypiernik.flixage.repository.ImageFileStore;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import com.zbutwialypiernik.flixage.service.resource.image.ImageProcessingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for images, here are defined processing services for each image type like:
 * thumbnail or artist background, in future possible expansion for more complicated processors
 * to optimize file size
 */
@Configuration
public class ImageConfiguration {

    @Bean
    public ImageFileService thumbnailImageService(ImageFileStore imageFileStore) {
        return new ImageFileService(imageFileStore, new ImageProcessingService(512, 512));
    }

}
