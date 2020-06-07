package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.repository.AlbumRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService extends QueryableService<Album> {

    public AlbumService(AlbumRepository repository, ImageFileService thumbnailService) {
        super(repository, thumbnailService);
    }

    public List<Album> getByArtistId(String artistId) {
        return getRepository().findByArtistId(artistId);
    }

    @Override
    protected AlbumRepository getRepository() {
        return (AlbumRepository) super.getRepository();
    }

}
