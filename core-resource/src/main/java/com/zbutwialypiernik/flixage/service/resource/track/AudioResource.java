package com.zbutwialypiernik.flixage.service.resource.track;

import com.zbutwialypiernik.flixage.service.resource.AbstractResource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

public class AudioResource implements AbstractResource {

    public static final Set<String> ACCEPTED_TYPES = Set.of("audio/mpeg", "audio/wav");

    public static final Set<String> ACCEPTED_EXTENSIONS = Set.of("mp3", "wav");

    public static final int MAX_FILE_SIZE = (int) FileUtils.ONE_MB * 15;

    private final byte[] content;
    private final String fileName;
    private final String extension;
    private final String mimeType;

    public AudioResource(byte[] content, String fileName, String mimeType) {
        this.content = content;
        this.fileName = fileName;
        this.extension = FilenameUtils.getExtension(fileName);
        this.mimeType = mimeType;
    }

    public AudioResource(byte[] content, String fileName, String extension, String mimeType) {
        this.content = content;
        this.fileName = fileName;
        this.extension = extension;
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

    @Override
    public Collection<String> getAcceptedExtensions() {
        return ACCEPTED_EXTENSIONS;
    }

}
