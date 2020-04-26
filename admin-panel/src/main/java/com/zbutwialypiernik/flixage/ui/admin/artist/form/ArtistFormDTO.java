package com.zbutwialypiernik.flixage.ui.admin.artist.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ArtistFormDTO {

    private String id;

    @Size(min = 5, max = 32, message = "")
    private String name;

    @NotNull
    @NotEmpty
    private byte[] artistAvatar;

}
