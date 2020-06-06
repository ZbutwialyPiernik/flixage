package com.zbutwialypiernik.flixage.ui.component;

import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;

public class ComponentUtils {

    public static AbstractStreamResource imageFromByteArray(ImageResource resource) {
        return new StreamResource(resource.getFileName() + "." + resource.getExtension(), resource::getInputStream);
    }

}
