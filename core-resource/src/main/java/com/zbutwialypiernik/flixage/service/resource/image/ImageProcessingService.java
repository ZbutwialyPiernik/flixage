package com.zbutwialypiernik.flixage.service.resource.image;


import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Service that resizes image to max outputWidth and outputHeight, but keeping aspect ratio of mage
 * eg: outputWidth=200 outputHeight=200
 * image 400x360 will be resized to 200x180
 */
public class ImageProcessingService {

    private final int outputWidth;
    private final int outputHeight;

    public ImageProcessingService(int outputWidth, int outputHeight) {
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
    }

    public ImageResource process(ImageResource imageResource) {
        try (var outputStream = new ByteArrayOutputStream(); var inputStream = imageResource.getInputStream()){
            Thumbnails.of(inputStream)
                    .size(outputWidth, outputHeight)
                    .outputFormat(imageResource.getExtension())
                    .toOutputStream(outputStream);

            return new ImageResource(outputStream.toByteArray(), imageResource.getFileName(), imageResource.getExtension(), imageResource.getMimeType());
        } catch (IOException e) {
            e.printStackTrace();

            throw new RuntimeException("Problem during transcoding of image");
        }
    }

}
