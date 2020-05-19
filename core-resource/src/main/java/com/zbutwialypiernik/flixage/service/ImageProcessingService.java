package com.zbutwialypiernik.flixage.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class ImageProcessingService {

    private static final int IMAGE_SIZE = 256;

    public ImageResource process(ImageResource imageResource) {
        try (InputStream inputStream = imageResource.getInputStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(bufferedImage, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
            g2d.dispose();

            ImageIO.write(bufferedImage, imageResource.getExtension(), outputStream);

            return new ImageResource() {
                @Override
                public InputStream getInputStream() {
                    return new ByteArrayInputStream(outputStream.toByteArray());
                }

                @Override
                public String getName() {
                    return imageResource.getName();
                }

                @Override
                public String getExtension() {
                    return imageResource.getExtension();
                }

                @Override
                public String getMimeType() {
                    return imageResource.getMimeType();
                }
            };
        } catch (IOException e) {
            e.printStackTrace();

            throw new RuntimeException("Problem during transcoding of image");
        }
    }

}
