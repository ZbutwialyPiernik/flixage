package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.repository.AlbumRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public Page<Album> getRecentlyAdded(int offset, int limit) {
        return getRepository().findAllByOrderByCreationTimeDesc(PageRequest.of(offset / limit, limit));
    }

    @Override
    protected AlbumRepository getRepository() {
        return (AlbumRepository) super.getRepository();
    }

}
