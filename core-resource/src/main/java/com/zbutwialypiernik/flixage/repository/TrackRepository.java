package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.entity.Track;

import java.util.List;

public interface TrackRepository extends QueryableRepository<Track> {

    List<Track> findByArtistId(String artistId);

    // Query that finds singles of artist
    List<Track> findByArtistIdAndAlbumIsNull(String artistId);

    List<Track> findByAlbumId(String albumId);

}
