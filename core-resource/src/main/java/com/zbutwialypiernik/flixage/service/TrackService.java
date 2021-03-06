package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.file.AudioFileEntity;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.TrackRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;
import com.zbutwialypiernik.flixage.service.resource.track.AudioResource;
import com.zbutwialypiernik.flixage.service.resource.track.TrackFileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public Optional<ImageResource> findThumbnailById(String id, boolean mustHaveAudioFile) {
        var entity = findById(id).orElseThrow(ResourceNotFoundException::new);

        if (mustHaveAudioFile && entity.getAudioFile() == null) {
            throw new ResourceNotFoundException();
        }

        return thumbnailService.get(entity.getThumbnail());
    }

    public Page<Track> getRecentlyAdded(int offset, int limit) {
        return getRepository().findByAudioFileIsNotNullOrderByCreationTimeDesc(PageRequest.of(offset / limit, limit));
    }

    public AudioResource getTrackFile(String id) {
        Track track = findById(id).orElseThrow(ResourceNotFoundException::new);

        return trackFileService.get(track.getAudioFile()).orElseThrow(ResourceNotFoundException::new);
    }

    public Page<Track> findByName(String name, int offset, int limit, boolean mustHaveAudioFile) {
        return mustHaveAudioFile
                ? getRepository().findByNameContainingIgnoreCaseAndAudioFileIsNotNull(name, PageRequest.of(offset / limit, limit))
                : super.findByName(name, offset, limit);
    }

    public List<Track> getArtistSingles(String artistId, boolean mustHaveAudioFile) {
        return mustHaveAudioFile
                ? getRepository().findByArtistIdAndAlbumIsNullAndAudioFileIsNotNull(artistId)
                : getRepository().findByArtistIdAndAlbumIsNull(artistId);
    }

    public List<Track> getTracksByArtistId(String artistId, boolean mustHaveAudioFile) {
        return mustHaveAudioFile
                ? getRepository().findByArtistIdAndAudioFileIsNotNull(artistId)
                : getRepository().findByArtistId(artistId);
    }

    public List<Track> getTracksByAlbumId(String albumId, boolean mustHaveAudioFile) {
        return mustHaveAudioFile
                ? getRepository().findByAlbumIdAndAudioFileIsNotNull(albumId)
                : getRepository().findByAlbumId(albumId);
    }

    @Override
    protected TrackRepository getRepository() {
        return (TrackRepository) super.getRepository();
    }
}
