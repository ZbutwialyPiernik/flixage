package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.repository.ThumbnailStore;
import com.zbutwialypiernik.flixage.repository.TrackRepository;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class TrackService extends QueryableService<Track> {

    public TrackService(TrackRepository repository, ThumbnailStore<Track> store, Clock clock)  {
        super(repository, store, clock);
    }


}
