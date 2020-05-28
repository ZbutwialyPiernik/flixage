package com.zbutwialypiernik.flixage.service;


import com.zbutwialypiernik.flixage.service.file.resource.ImageResource;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class ImageProcessingService {

    private static final int SIZE = 256;

    public ImageResource process(ImageResource imageResource) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            Thumbnails.of(imageResource.getInputStream())
                    .size(SIZE, SIZE)
                    .outputFormat(imageResource.getExtension())
                    .toOutputStream(outputStream);

            return new ImageResource(outputStream.toByteArray(), imageResource.getName(), imageResource.getExtension(), imageResource.getMimeType());
        } catch (IOException e) {
            e.printStackTrace();

            throw new RuntimeException("Problem during transcoding of image");
        }
    }

}
