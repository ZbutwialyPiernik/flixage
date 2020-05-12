package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.repository.AlbumRepository;
import com.zbutwialypiernik.flixage.repository.ThumbnailFileStore;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class AlbumService extends QueryableService<Album> {

    private final AlbumRepository repository;

    public AlbumService(AlbumRepository repository, ThumbnailFileStore store, Clock clock) {
        super(repository, store, clock);
        this.repository = repository;
    }

}
