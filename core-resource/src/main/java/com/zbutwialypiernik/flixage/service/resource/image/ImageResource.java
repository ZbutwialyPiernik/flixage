package com.zbutwialypiernik.flixage.service.resource.image;

import com.zbutwialypiernik.flixage.service.resource.AbstractResource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ImageResource implements AbstractResource {

    public static final Set<String> ACCEPTED_TYPES = Set.of("image/jpeg", "image/png");

    public static final Set<String> ACCEPTED_EXTENSIONS = Set.of("jpg", "png", "jpeg");

    public static final int MAX_FILE_SIZE = (int) FileUtils.ONE_MB * 2;

    private final byte[] content;
    private final String fileName;
    private final String extension;
    private final String mimeType;

    private BufferedImage bufferedImage;

    public ImageResource(byte[] content, String fileName, String extension, String mimeType) {
        this.content = content;
        this.fileName = fileName;
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public ImageResource(byte[] content, String fileName, String mimeType) {
        this.content = content;
        this.fileName = fileName;
        this.extension = FilenameUtils.getExtension(fileName);
        this.mimeType = mimeType;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    public int getWidth() {
        synchronized (this) {
            if (bufferedImage == null) {
                initBufferedImage();
            }
        }

        return bufferedImage.getWidth();
    }

    public int getHeight() {
        synchronized (this) {
            if (bufferedImage == null) {
                initBufferedImage();
            }
        }

        return bufferedImage.getHeight();
    }

    private void initBufferedImage() {
        try (InputStream inputStream = getInputStream()) {
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
