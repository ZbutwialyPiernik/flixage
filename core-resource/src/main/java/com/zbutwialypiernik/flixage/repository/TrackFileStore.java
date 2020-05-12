package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Track;
import org.springframework.content.commons.repository.ContentStore;

public interface TrackFileStore extends ContentStore<Track, byte[]> {
}
