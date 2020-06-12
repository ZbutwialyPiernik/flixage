package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Every track will be visible in admin panel, but in frontend with normal users
 * only tracks with available audio file will be visible
 */
public interface TrackRepository extends QueryableRepository<Track> {

    //
    Page<Track> findByNameContainingIgnoreCaseAndAudioFileIsNotNull(String name, Pageable pageable);
    
    //
    List<Track> findByArtistIdAndAudioFileIsNotNull(String artistId);

    List<Track> findByArtistId(String artistId);

    // Query that finds singles of artist
    List<Track> findByArtistIdAndAlbumIsNullAndAudioFileIsNotNull(String artistId);

    List<Track> findByArtistIdAndAlbumIsNull(String artistId);

    //
    List<Track> findByAlbumIdAndAudioFileAndIsNotNull(String albumId);

    List<Track> findByAlbumId(String albumId);

}
