package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.PlaylistRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlaylistService extends QueryableService<Playlist> {

    private final TrackService trackService;

    @Autowired
    public PlaylistService(PlaylistRepository repository, TrackService trackService, ImageFileService thumbnailService) {
        super(repository, thumbnailService);
        this.trackService = trackService;
    }

    public List<Playlist> findByUserId(String id) {
        return getRepository().findByOwnerId(id);
    }

    @Transactional
    public List<Track> getTracks(String playlistId) {
        Playlist playlist = getRepository().findById(playlistId).orElseThrow(ResourceNotFoundException::new);

        Hibernate.initialize(playlist.getTracks());

        return playlist.getTracks();
    }

    @Transactional
    public void addTracks(Playlist playlist, Set<String> trackIds) {
        List<Track> tracks = trackIds
                .stream()
                .map(id -> trackService.findById(id).orElseThrow(() -> new BadRequestException("Track with id " + id + "does not exists")))
                .collect(Collectors.toList());

        Hibernate.initialize(playlist.getTracks());

        playlist.getTracks().addAll(tracks);
    }

    /**
     * Removes tracks from playlist by id
     *
     * @throws BadRequestException when playlist does not contain given track
     * @throws ResourceNotFoundException when playlist with given id doesn't exists
     *
     * @param playlist playlist to remove tracks
     * @param trackIds the ids of tracks to remove
     */
    @Transactional
    public void removeTracks(Playlist playlist, Set<String> trackIds) {
        Hibernate.initialize(playlist.getTracks());

        for (var id : trackIds) {
            if (!playlist.getTracks().removeIf(track -> track.getId().equals(id))) {
                throw new BadRequestException("Playlist does not contain track with id: " + id);
            }
        }
    }

    @Transactional
    public void followPlaylist(User user, String playlistId) {
        final var playlist = findById(playlistId).orElseThrow(ResourceNotFoundException::new);

        if (playlist.getOwner().equals(user)) {
            throw new BadRequestException("User cannot follow his own playlist");
        }

        playlist.getFollowers().add(user);
    }

    @Transactional
    public void unfollowPlaylist(User user, String playlistId) {
        final var playlist = findById(playlistId).orElseThrow(ResourceNotFoundException::new);

        if (playlist.getOwner().equals(user)) {
            throw new BadRequestException("User cannot follow his own playlist");
        }

        playlist.getFollowers().remove(user);
    }

    public List<Playlist> findFollowedPlaylists(String userId) {
        return getRepository().findByFollowers_Id(userId);
    }

    @Override
    protected PlaylistRepository getRepository() {
        return (PlaylistRepository) super.getRepository();
    }

}
