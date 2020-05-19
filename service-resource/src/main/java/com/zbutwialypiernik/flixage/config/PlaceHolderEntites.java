package com.zbutwialypiernik.flixage.config;

import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.MusicGenre;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.repository.TrackFileStore;
import com.zbutwialypiernik.flixage.service.AlbumService;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.service.TrackService;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Stream;

@Component
public class PlaceHolderEntites {

    private final ArtistService artistService;

    private final TrackService trackService;

    private final AlbumService albumService;

    private final TrackFileStore trackFileStore;

    private final FileSystemResourceLoader resourceLoader;

    public PlaceHolderEntites(ArtistService artistService, TrackService trackService, AlbumService albumService, TrackFileStore trackFileStore, FileSystemResourceLoader resourceLoader) throws IOException {
        this.artistService = artistService;
        this.trackService = trackService;
        this.albumService = albumService;
        this.trackFileStore = trackFileStore;
        this.resourceLoader = resourceLoader;

        if (!artistService.findByName("rocky", 0, 10).isEmpty()) {

        }

        Artist asap = new Artist();
        asap.setName("A$AP Rocky");

        Artist linkinPark = new Artist();
        linkinPark.setName("Linkin Park");

        asap = artistService.create(asap);
        linkinPark = artistService.create(linkinPark);

        Album meteora = new Album();
        meteora.setArtist(linkinPark);
        meteora.setName("Meteora");

        albumService.create(meteora);

        Track sundress = new Track();
        sundress.setArtist(asap);
        sundress.setName("Sundress");

        Track praiseTheLord = new Track();
        praiseTheLord.setArtist(asap);
        praiseTheLord.setName("Praise the Lord");

        trackService.create(sundress);
        trackService.create(praiseTheLord);

        trackService.saveTrackFile(sundress, resourceLoader.getResource("Sundress.mp3").getFile());
        trackService.saveTrackFile(praiseTheLord, resourceLoader.getResource("Praise The Lord.mp3").getFile());

        Stream.of("Foreword", "Don't Stay", "Somewhere I Belong", "Lying from You",
                "Hit the Floor", "Easier to Run", "Faint", "Figure.09",
                "Breaking the Habit", "From the Inside", "Nobody's Listening",
                "Session", "Numb")
                .forEach((name) -> {
                    Track track = new Track();
                    track.setName(name);
                    track.setGenre(MusicGenre.ROCK);
                    track.setArtist(meteora.getArtist());
                    track.setAlbum(meteora);

                    trackService.create(track);
                });
    }
}
