package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.*;
import com.zbutwialypiernik.flixage.repository.ArtistRepository;
import com.zbutwialypiernik.flixage.repository.ThumbnailFileStore;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class ArtistService extends QueryableService<Artist> {

    public ArtistService(ArtistRepository repository, ThumbnailFileStore store, ImageProcessingService imageService, Clock clock) {
        super(repository, store, imageService, clock);
    }

}
