package com.zbutwialypiernik.flixage.ui.admin.artist.form;

import com.zbutwialypiernik.flixage.entity.MusicGenre;
import lombok.Data;

@Data
public class SongFormDTO {

    private String id;

    private String name;

    private MusicGenre genre;

    private byte[] song;

}
