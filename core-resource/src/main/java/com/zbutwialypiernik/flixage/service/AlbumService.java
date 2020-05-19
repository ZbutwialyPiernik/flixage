package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.repository.AlbumRepository;
import com.zbutwialypiernik.flixage.repository.ThumbnailFileStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class AlbumService extends QueryableService<Album> {

    private final AlbumRepository repository;

    public AlbumService(AlbumRepository repository, ThumbnailFileStore store, ImageProcessingService imageService, Clock clock) {
        super(repository, store, imageService, clock);
        this.repository = repository;
    }

    public Page<Album> findByArtistId(String artistId, int offset, int size) {
        return repository.findAlbumsByArtistId(artistId, PageRequest.of(offset * size, size));
    }

}
