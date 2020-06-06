package com.zbutwialypiernik.flixage.service.resource;

import java.io.InputStream;
import java.util.Collection;

public interface AbstractResource {

    InputStream getInputStream();

    byte[] getContent();

    String getFileName();

    String getExtension();

    String getMimeType();

    Collection<String> getAcceptedExtensions();

}
