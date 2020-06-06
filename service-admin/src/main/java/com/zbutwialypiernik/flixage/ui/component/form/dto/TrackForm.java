package com.zbutwialypiernik.flixage.ui.component.form.dto;

import com.zbutwialypiernik.flixage.entity.MusicGenre;
import com.zbutwialypiernik.flixage.service.resource.track.AudioResource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrackForm extends QueryableForm {

    private AudioResource audioResource;

    private MusicGenre genre;

}
