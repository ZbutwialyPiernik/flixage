package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.repository.ArtistRepository;
import com.zbutwialypiernik.flixage.repository.QueryableRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ArtistService extends QueryableService<Artist> {

    public ArtistService(ArtistRepository repository, ImageFileService thumbnailService) {
        super(repository, thumbnailService);
    }

    public Page<Artist> getRecentlyAdded(int offset, int limit) {
        return getRepository().findAllByOrderByCreationTimeDesc(PageRequest.of(offset / limit, limit));
    }

    @Override
    protected ArtistRepository getRepository() {
        return (ArtistRepository) super.getRepository();
    }
}
