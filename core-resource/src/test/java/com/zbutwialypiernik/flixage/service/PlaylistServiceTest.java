package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.PlaylistRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class PlaylistServiceTest {

    @Mock
    PlaylistRepository playlistRepository;

    @Mock
    TrackService trackService;

    @Mock
    ImageFileService thumbnailService;

    Clock clock = Clock.systemUTC();

    PlaylistService playlistService;

    Playlist playlist;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        playlist = new Playlist();
        playlist.setId(UUID.randomUUID().toString());
        playlist.setName("Playlist name");

        playlistService = new PlaylistService(playlistRepository, trackService, thumbnailService);
    }

    @Test
    public void can_get_tracks_when_playlist_does_exists() {
        final var trackId1 = UUID.randomUUID().toString();
        final var trackId2 = UUID.randomUUID().toString();

        final var track1 = new Track();
        track1.setId(trackId1);

        final var track2 = new Track();
        track2.setId(trackId2);

        playlist.getTracks().add(track1);
        playlist.getTracks().add(track2);

        when(playlistRepository.findById(any())).thenReturn(Optional.of(playlist));

        playlistService.getTracks(playlist.getId());

        assertEquals(playlist.getTracks().size(), 2);
    }

    @Test
    public void cannot_get_tracks_when_playlist_does_not_exists() {
        when(playlistRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> playlistService.getTracks("id"));
    }

    @Test
    public void tracks_get_added_to_playlist() {
        final var trackId1 = UUID.randomUUID().toString();
        final var trackId2 = UUID.randomUUID().toString();

        final var track1 = new Track();
        track1.setId(trackId1);

        final var track2 = new Track();
        track2.setId(trackId2);

        when(trackService.findById(trackId1)).thenReturn(Optional.of(track1));
        when(trackService.findById(trackId2)).thenReturn(Optional.of(track2));

        var oldPlaylist = new Playlist();
        oldPlaylist.setId(playlist.getId());

        when(playlistService.findById(playlist.getId())).thenReturn(Optional.of(oldPlaylist));

        playlistService.addTracks(playlist, Set.of(trackId1, trackId2));

        assertEquals(playlist.getTracks().size(), 2);
    }

    @Test
    public void tracks_does_not_get_added_to_playlist_when_track_is_missing() {
        final var trackId1 = UUID.randomUUID().toString();
        final var trackId2 = UUID.randomUUID().toString();

        final var track1 = new Track();
        track1.setId(trackId1);

        when(trackService.findById(trackId1)).thenReturn(Optional.of(track1));

        assertThrows(ResourceNotFoundException.class,
                () -> playlistService.addTracks(playlist, Set.of(trackId1, trackId2)));

        assertEquals(playlist.getTracks().size(), 0);
    }

    @Test
    public void tracks_get_removed_from_playlist() {
        final var trackId1 = UUID.randomUUID().toString();
        final var trackId2 = UUID.randomUUID().toString();

        final var track1 = new Track();
        track1.setId(trackId1);

        final var track2 = new Track();
        track2.setId(trackId2);

        playlist.getTracks().add(track1);
        playlist.getTracks().add(track2);

        var oldPlaylist = new Playlist();
        oldPlaylist.setId(playlist.getId());

        when(playlistService.findById(playlist.getId())).thenReturn(Optional.of(oldPlaylist));

        playlistService.removeTracks(playlist, Set.of(trackId1, trackId2));

        assertEquals(playlist.getTracks().size(), 0);
    }

    @Test
    public void tracks_does_not_get_removed_from_playlist_when_track_is_not_in_playlist() {
        final var trackId1 = UUID.randomUUID().toString();
        final var trackId2 = UUID.randomUUID().toString();

        final var track1 = new Track();
        track1.setId(trackId1);

        playlist.getTracks().add(track1);

        assertThrows(BadRequestException.class,
                () -> playlistService.removeTracks(playlist, Set.of(trackId1, trackId2)));
    }

    @Test
    public void tracks_does_not_get_removed_from_playlist_when_playlist_is_not_in_database() {
        final var trackId1 = UUID.randomUUID().toString();
        final var trackId2 = UUID.randomUUID().toString();

        final var track1 = new Track();
        track1.setId(trackId1);

        final var track2 = new Track();
        track2.setId(trackId2);

        playlist.getTracks().add(track1);
        playlist.getTracks().add(track2);

        when(playlistRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> playlistService.removeTracks(playlist, Set.of(trackId1, trackId2)));
    }

}
