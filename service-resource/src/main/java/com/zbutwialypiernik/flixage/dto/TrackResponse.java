package com.zbutwialypiernik.flixage.dto;

import com.zbutwialypiernik.flixage.entity.MusicGenre;
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

    private long playCount;

}
