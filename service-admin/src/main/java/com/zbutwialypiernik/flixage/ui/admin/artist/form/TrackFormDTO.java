package com.zbutwialypiernik.flixage.ui.admin.artist.form;

import com.zbutwialypiernik.flixage.entity.MusicGenre;
import com.zbutwialypiernik.flixage.ui.component.form.dto.QueryableFormDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrackFormDTO extends QueryableFormDTO {

    private MusicGenre genre;

}
