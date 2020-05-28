package com.zbutwialypiernik.flixage.ui.component.form.dto;

import com.zbutwialypiernik.flixage.service.file.resource.ImageResource;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public abstract class QueryableFormDTO {

    private String id;

    @Size(min = 5, max = 32, message = "")
    private String name;

    private ImageResource thumbnailResource;

}
