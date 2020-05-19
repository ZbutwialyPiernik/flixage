package com.zbutwialypiernik.flixage.ui.component.form.dto;

import com.zbutwialypiernik.flixage.service.ImageResource;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Size;
import java.io.File;

@Data
public class QueryableFormDTO {

    private String id;

    @Size(min = 5, max = 32, message = "")
    private String name;

    private ImageResource thumbnailResource;

}
