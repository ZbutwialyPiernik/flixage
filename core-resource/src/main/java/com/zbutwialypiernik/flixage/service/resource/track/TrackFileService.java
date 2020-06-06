package com.zbutwialypiernik.flixage.service.resource.track;

import com.zbutwialypiernik.flixage.entity.file.AudioFileEntity;
import com.zbutwialypiernik.flixage.repository.TrackFileStore;
import com.zbutwialypiernik.flixage.service.resource.AbstractResourceService;
import lombok.extern.log4j.Log4j2;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


@Log4j2
@Service
public class TrackFileService extends AbstractResourceService<AudioFileEntity, AudioResource> {

    public TrackFileService(TrackFileStore store) {
        super(store);
    }

    @Override
    public void saveMetadata(AudioFileEntity entity, AudioResource resource) {
        try {
            File file = store.getResource(entity).getFile();

            AudioFile audioFile = AudioFileIO.read(file);
            entity.setDuration(audioFile.getAudioHeader().getTrackLength());
        } catch (CannotReadException | InvalidAudioFrameException | ReadOnlyFileException | TagException | IOException e) {
            log.error("Error during metadata save: ", e);
        }
    }

    @Override
    protected AudioResource createResource(InputStream inputStream, AudioFileEntity entity) throws IOException {
        return new AudioResource(inputStream.readAllBytes(), entity.getFileId(), entity.getExtension(), entity.getMimeType());
    }

}
