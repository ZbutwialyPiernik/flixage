package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.repository.AlbumRepository;
import com.zbutwialypiernik.flixage.service.file.ThumbnailFileService;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
public class AlbumService extends QueryableService<Album> {

    private final AlbumRepository repository;

    public AlbumService(AlbumRepository repository, ThumbnailFileService thumbnailService, Clock clock) {
        super(repository, thumbnailService, clock);
        this.repository = repository;
    }

    public List<Album> getByArtistId(String artistId) {
        return repository.findByArtistId(artistId);
    }

}
