package com.zbutwialypiernik.flixage.ui.component;

import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.zbutwialypiernik.flixage.service.file.resource.ImageResource;

public class ComponentUtils {

    public static AbstractStreamResource imageFromByteArray(ImageResource resource) {
        return new StreamResource(resource.getName() + "." + resource.getExtension(), resource::getInputStream);
    }

}
