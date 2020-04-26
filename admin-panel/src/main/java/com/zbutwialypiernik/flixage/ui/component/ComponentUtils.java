package com.zbutwialypiernik.flixage.ui.component;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;

public class ComponentUtils {

    /**
     * Factory method to create images in one line from byte array
     * @param image byte array of image
     * @param name name of image file
     * @param alt alternative image text
     * @return
     */
    public static Image imageFromByteArray(byte[] image, String name, String alt) {
        return new Image(
                new StreamResource(name, () -> new ByteArrayInputStream(image)),
                alt);
    }

    //public static Upload createUpload() {

    //}

}
