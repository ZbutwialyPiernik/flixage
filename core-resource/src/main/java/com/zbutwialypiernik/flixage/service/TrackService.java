package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.exception.ResourceLoadingException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.TrackFileStore;
import com.zbutwialypiernik.flixage.repository.TrackRepository;
import com.zbutwialypiernik.flixage.service.file.ThumbnailFileService;
import com.zbutwialypiernik.flixage.service.file.TrackFileService;
import com.zbutwialypiernik.flixage.service.file.resource.AudioResource;
import org.springframework.stereotype.Service;
import ws.schild.jave.*;

import java.io.*;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class TrackService extends QueryableService<Track> {

    private final TrackFileService trackFileService;
    private final TrackRepository repository;

    public TrackService(TrackRepository repository, ThumbnailFileService thumbnailService, TrackFileService trackFileService, Clock clock)  {
        super(repository, thumbnailService, clock);
        this.trackFileService = trackFileService;
        this.repository = repository;
    }

    public AudioResource getTrackFile(String id) {
        Track track = findById(id).orElseThrow(ResourceNotFoundException::new);

        return trackFileService.get(track).orElseThrow(ResourceNotFoundException::new);
    }

    public void saveTrackFile(Track track, AudioResource resource) {
        trackFileService.save(track, resource);

        super.update(track);
    }

    /**
     * Returns singles by artist id
     * @param artistId
     * @return
     */
    public List<Track> getSingles(String artistId) {
        return repository.findByArtistIdAndAlbumIsNull(artistId);
    }

    public List<Track> getTracksByArtistId(String artistId) {
        return repository.findByArtistId(artistId);
    }

}
