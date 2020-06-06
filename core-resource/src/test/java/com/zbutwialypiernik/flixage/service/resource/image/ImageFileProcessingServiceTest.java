package com.zbutwialypiernik.flixage.service.resource.image;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageFileProcessingServiceTest {

    public static final int OUTPUT_WIDTH = 200;
    public static final int OUTPUT_HEIGHT = 200;

    public ImageProcessingService imageProcessingService = new ImageProcessingService(OUTPUT_WIDTH, OUTPUT_HEIGHT);

    @Test
    public void image_gets_resized() throws IOException, URISyntaxException {
        var testImage = "image_to_resize.jpg";
        byte[] imageArray = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(testImage).toURI()));

        ImageResource imageToProcess = new ImageResource(imageArray, testImage, "jpg", "image/jpeg");

        ImageResource outputImage = imageProcessingService.process(imageToProcess);

        Assertions.assertTrue(outputImage.getWidth() > 1 && outputImage.getWidth() <= OUTPUT_WIDTH);
        Assertions.assertTrue(outputImage.getHeight() > 1 && outputImage.getHeight() <= OUTPUT_HEIGHT);
    }

}
