package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.ThumbnailFileStore;
import com.zbutwialypiernik.flixage.repository.TrackFileStore;
import com.zbutwialypiernik.flixage.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;

@Service
public class TrackService extends QueryableService<Track> {

    private final TrackFileStore trackFileStore;

    public TrackService(TrackRepository repository, ThumbnailFileStore thumbnailFileStore, TrackFileStore trackFileStore, Clock clock)  {
        super(repository, thumbnailFileStore, clock);
        this.trackFileStore = trackFileStore;
    }

    public byte[] getTrackFile(String id) {
        try {
            File file = trackFileStore.getResource(findById(id)).getFile();

            if (!file.exists()) {
                throw new ResourceNotFoundException("File not found");
            }

            return Files.readAllBytes(Path.of(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
