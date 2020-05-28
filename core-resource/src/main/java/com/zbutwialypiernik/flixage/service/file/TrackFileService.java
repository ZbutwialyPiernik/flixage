package com.zbutwialypiernik.flixage.service.file;

import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.exception.ResourceLoadingException;
import com.zbutwialypiernik.flixage.exception.UnsupportedMediaTypeException;
import com.zbutwialypiernik.flixage.repository.TrackFileStore;
import com.zbutwialypiernik.flixage.service.file.resource.AudioResource;
import com.zbutwialypiernik.flixage.service.file.resource.ImageResource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import util.ExtensionUtils;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Log4j2
@Service
public class TrackFileService implements FileService<Track, AudioResource> {

    private final TrackFileStore store;

    public TrackFileService(TrackFileStore store) {
        this.store = store;
    }

    public void save(Track track, AudioResource resource) {
        final String originalExtension = resource.getExtension();

        if (Arrays.stream(ImageResource.ACCEPTED_EXTENSIONS)
                .noneMatch((type) -> type.equals(originalExtension))) {
            throw new UnsupportedMediaTypeException("Unsupported file extension: " + resource.getExtension());
        }

        try (InputStream inputStream = resource.getInputStream()) {
            track.setExtension(originalExtension);
            track.setMimeType(ExtensionUtils.getMimeType(originalExtension));

            store.setContent(track, inputStream);

            var info = new MultimediaObject(store.getResource(track).getFile()).getInfo();
            track.setDuration(Duration.ofMillis(info.getDuration()));
        } catch (IOException | EncoderException e) {
            e.printStackTrace();
        }
    }

    public void delete(Track track) {
        store.unsetContent(track);
    }

    public Optional<AudioResource> get(Track track) {
        if (track == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(new AudioResource(store.getContent(track).readAllBytes(), track.getFileId(), track.getExtension(), track.getMimeType()));
        } catch (IOException e) {
            log.error("Problem during loading of audio resource", e);
            throw new ResourceLoadingException("Problem during loading of audio resource");
        }
    }

}
