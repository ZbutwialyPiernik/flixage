package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.repository.QueryableRepository;
import com.zbutwialypiernik.flixage.service.resource.image.ImageFileService;
import org.springframework.stereotype.Service;

@Service
public class ArtistService extends QueryableService<Artist> {

    public ArtistService(QueryableRepository<Artist> repository, ImageFileService thumbnailService) {
        super(repository, thumbnailService);
    }

}
