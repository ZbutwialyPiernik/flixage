package com.zbutwialypiernik.flixage.dto;

import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.MusicGenre;
import com.zbutwialypiernik.flixage.repository.ArtistRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TrackResponse extends QueryableResponse {

    private ArtistResponse artist;

    private MusicGenre genre;

    private long duration;

}
