package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.*;
import com.zbutwialypiernik.flixage.repository.ArtistRepository;
import com.zbutwialypiernik.flixage.repository.ThumbnailStore;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.time.Clock;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ArtistService extends QueryableService<Artist> {

    private final ArtistRepository repository;

    public ArtistService(ArtistRepository repository, ThumbnailStore store, Clock clock) {
        super(repository, store, clock);
        this.repository = repository;
    }

    /**
     * Finds artist by id and fetches eagerly children entities
     * @param artistId
     * @return optional
     */
    @Transactional
    public Optional<Artist> findByIdEagerly(String artistId) {
        Optional<Artist> artistOptional =  repository.findById(artistId);

        artistOptional.ifPresent(artist -> {
            Hibernate.initialize(artist.getTracks());
            Hibernate.initialize(artist.getAlbums());

            artist.getAlbums().forEach(album -> Hibernate.initialize(album.getTracks()));
        });

        return artistOptional;
    }

}
