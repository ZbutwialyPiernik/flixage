package com.zbutwialypiernik.flixage.service.resource;

import java.io.InputStream;

public interface AbstractResource {

    InputStream getInputStream();

    byte[] getContent();

    String getFileName();

    String getExtension();

    String getMimeType();

}
