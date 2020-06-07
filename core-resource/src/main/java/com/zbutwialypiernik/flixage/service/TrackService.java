package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.file.AudioFileEntity;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.QueryableRepository;
import com.zbutwialypiernik.flixage.repository.TrackRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import com.zbutwialypiernik.flixage.service.resource.track.AudioResource;
import com.zbutwialypiernik.flixage.service.resource.track.TrackFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TrackService extends QueryableService<Track> {

    private final TrackFileService trackFileService;

    public TrackService(TrackRepository repository, ImageFileService thumbnailService, TrackFileService trackFileService)  {
        super(repository, thumbnailService);
        this.trackFileService = trackFileService;
    }

    public AudioResource getTrackFile(String id) {
        Track track = findById(id).orElseThrow(ResourceNotFoundException::new);

        return trackFileService.get(track.getAudioFile()).orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional
    public void saveTrackFile(Track track, AudioResource resource) {
        if (!getRepository().existsById(track.getId())) {
            throw new ResourceNotFoundException();
        }

        if (track.getAudioFile() == null) {
            track.setAudioFile(new AudioFileEntity());
            track.getAudioFile().setId(UUID.randomUUID().toString());
        }

        trackFileService.save(track.getAudioFile(), resource);

        getRepository().save(track);
    }

    @Transactional
    public void increasePlayCount(String trackId) {
        Track track = getRepository().findById(trackId).orElseThrow(ResourceNotFoundException::new);

        track.setPlayCount(track.getPlayCount() + 1);
    }

    public List<Track> getArtistSingles(String artistId) {
        return getRepository().findByArtistIdAndAlbumIsNull(artistId);
    }

    public List<Track> getTracksByArtistId(String artistId) {
        return getRepository().findByArtistId(artistId);
    }

    public List<Track> getTracksByAlbumId(String albumId) {
        return getRepository().findByAlbumId(albumId);
    }

    @Override
    protected TrackRepository getRepository() {
        return (TrackRepository) super.getRepository();
    }
}
