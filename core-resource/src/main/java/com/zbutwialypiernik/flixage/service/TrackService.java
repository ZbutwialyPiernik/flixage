package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.exception.ResourceLoadingException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.ThumbnailFileStore;
import com.zbutwialypiernik.flixage.repository.TrackFileStore;
import com.zbutwialypiernik.flixage.repository.TrackRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ws.schild.jave.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;

@Service
public class TrackService extends QueryableService<Track> {

    private final TrackFileStore trackFileStore;

    public TrackService(TrackRepository repository, ThumbnailFileStore thumbnailFileStore, ImageProcessingService imageService, TrackFileStore trackFileStore, Clock clock)  {
        super(repository, thumbnailFileStore, imageService, clock);
        this.trackFileStore = trackFileStore;
    }

    public byte[] getTrackFile(String id) {
        try {
            Resource resource = trackFileStore.getResource(super.findById(id).orElseThrow(ResourceNotFoundException::new));

            File file = resource.getFile();

            if (!file.exists()) {
                throw new ResourceNotFoundException("File not found");
            }

            return Files.readAllBytes(Path.of(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new ResourceLoadingException("Problem during loading of track");
        }
    }

    public void saveTrackFile(Track track, File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            MultimediaInfo fileInfo = new MultimediaObject(file).getInfo();

            trackFileStore.setContent(track, inputStream);
            track.setDuration(Duration.ofMillis(fileInfo.getDuration()));
        } catch (IOException | EncoderException e) {
            e.printStackTrace();
        }

        super.update(track);
    }

}
