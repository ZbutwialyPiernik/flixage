package com.zbutwialypiernik.flixage.ui.component.form.dto;

import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public abstract class QueryableForm {

    // Readonly field
    private String id;

    @Size(min = 5, max = 32, message = "Length should be between 5 and 23")
    private String name;

    private ImageResource thumbnailResource;

}
