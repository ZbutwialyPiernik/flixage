package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.file.AudioFileEntity;
import org.springframework.content.commons.repository.ContentStore;

public interface TrackFileStore extends ContentStore<AudioFileEntity, String> {
}
