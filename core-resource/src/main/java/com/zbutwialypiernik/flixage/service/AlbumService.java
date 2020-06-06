package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.repository.AlbumRepository;
import com.zbutwialypiernik.flixage.repository.TrackRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService extends QueryableService<Album> {

    private final AlbumRepository repository;

    public AlbumService(AlbumRepository repository, ImageFileService thumbnailService) {
        super(repository, thumbnailService);
        this.repository = repository;
    }

    public List<Album> getByArtistId(String artistId) {
        return repository.findByArtistId(artistId);
    }

}
