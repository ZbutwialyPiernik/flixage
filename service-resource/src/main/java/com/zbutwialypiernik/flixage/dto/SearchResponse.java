package com.zbutwialypiernik.flixage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.Album;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {

    private PageResponse<TrackResponse> tracks;

    private PageResponse<AlbumResponse> albums;

    private PageResponse<ArtistResponse> artists;

    private PageResponse<UserResponse> users;

    private PageResponse<PlaylistResponse> playlists;


}
