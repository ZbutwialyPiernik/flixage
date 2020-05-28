package com.zbutwialypiernik.flixage.service.file.resource;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AudioResource implements AbstractResource {

    String[] ACCEPTED_TYPES = new String[]{"audio/mp3", "audio/wav"};

    String[] ACCEPTED_EXTENSIONS = new String[]{"mp3", "wav"};

    long MAX_FILE_SIZE = FileUtils.ONE_MB * 15;

    private final byte[] content;
    private final String name;
    private final String extension;
    private final String mimeType;

    public AudioResource(byte[] content, String name, String extension, String mimeType) {
        this.content = content;
        this.name = name;
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
    public String getName() {
        return name;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

}
