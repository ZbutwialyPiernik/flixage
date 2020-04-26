package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.PlaylistRepository;
import com.zbutwialypiernik.flixage.repository.ThumbnailStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService extends QueryableService<Playlist> {

    private final PlaylistRepository playlistRepository;

    private final TrackService trackService;

    @Autowired
    public PlaylistService(PlaylistRepository repository, TrackService trackService, ThumbnailStore<Playlist> store, Clock clock) {
        super(repository, store, clock);
        this.playlistRepository = repository;
        this.trackService = trackService;
    }

    public List<Playlist> findByUserId(String id) {
        return playlistRepository.findByOwnerId(id);
    }

    @Transactional
    public void addTrackToPlaylistByIds(Playlist playlist, List<String> trackIds) {
        List<Track> tracks = trackIds
                .stream()
                .map((id) -> {
                    try {
                        return trackService.findById(id);
                    } catch (ResourceNotFoundException e) {
                        // We catch and rethrow a message with more detailed exception
                        throw new ResourceNotFoundException("Track with id " + id + "does not exists");
                    }
                }).collect(Collectors.toList());

        playlist.getTracks().addAll(tracks);
    }

    public void removeTrackFromPlaylistByIds(Playlist playlist, List<Track> tracks) {

    }

}
