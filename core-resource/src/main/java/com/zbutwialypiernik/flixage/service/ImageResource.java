package com.zbutwialypiernik.flixage.service;

import java.io.InputStream;

public interface ImageResource {

    InputStream getInputStream();

    String getName();

    String getExtension();

    String getMimeType();

}
