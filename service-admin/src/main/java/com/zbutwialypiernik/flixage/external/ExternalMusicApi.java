package com.zbutwialypiernik.flixage.external;

import java.util.List;

/**
 * Interface to import artists from external APIs like spotify API.
 */
public interface ExternalMusicApi {

    List<ExternalArtistData> searchArtist(String name);

    List<ExternalAlbumData> getAlbums(String artistId);

    /**
     *
     * @return the service name
     */
    String getName();

}
