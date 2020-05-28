package com.zbutwialypiernik.flixage.service.file.resource;

import java.io.InputStream;

public interface AbstractResource {

    InputStream getInputStream();

    byte[] getContent();

    String getName();

    String getExtension();

    String getMimeType();

}
