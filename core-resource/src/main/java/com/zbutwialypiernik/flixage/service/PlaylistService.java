package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.PlaylistRepository;
import com.zbutwialypiernik.flixage.service.file.ThumbnailFileService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService extends QueryableService<Playlist> {

    private final PlaylistRepository playlistRepository;

    private final TrackService trackService;

    @Autowired
    public PlaylistService(PlaylistRepository repository, TrackService trackService, ThumbnailFileService thumbnailService, Clock clock) {
        super(repository, thumbnailService, clock);
        this.playlistRepository = repository;
        this.trackService = trackService;
    }

    public List<Playlist> findByUserId(String id) {
        return playlistRepository.findByOwnerId(id);
    }

    @Transactional
    public List<Track> getTracksByPlaylistId(String playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        Hibernate.initialize(playlist.getTracks());

        return playlist.getTracks();
    }

    @Transactional
    public void addTrackToPlaylistByIds(Playlist playlist, List<String> trackIds) {
        List<Track> tracks = trackIds
                .stream()
                .map((id) ->
                     trackService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Track with id " + id + "does not exists"))
                ).collect(Collectors.toList());

        Hibernate.initialize(playlist.getTracks());

        playlist.getTracks().addAll(tracks);
    }

    /*
    public void removeTrackFromPlaylistByIds(Playlist playlist, List<Track> tracks) {

    }*/



}
