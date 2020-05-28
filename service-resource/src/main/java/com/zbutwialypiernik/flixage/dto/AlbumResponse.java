package com.zbutwialypiernik.flixage.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AlbumResponse extends QueryableResponse {

    private ArtistResponse artist;

}