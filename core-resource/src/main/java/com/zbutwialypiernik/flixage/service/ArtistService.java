package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.*;
import com.zbutwialypiernik.flixage.repository.QueryableRepository;
import com.zbutwialypiernik.flixage.service.file.ThumbnailFileService;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class ArtistService extends QueryableService<Artist> {

    public ArtistService(QueryableRepository<Artist> repository, ThumbnailFileService thumbnailService, Clock clock) {
        super(repository, thumbnailService, clock);
    }
}
