package com.zbutwialypiernik.flixage.service;

import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.TrackStream;
import com.zbutwialypiernik.flixage.entity.TrackStreamId;
import com.zbutwialypiernik.flixage.exception.AuthenticationException;
import com.zbutwialypiernik.flixage.exception.ConflictException;
import com.zbutwialypiernik.flixage.exception.ResourceNotFoundException;
import com.zbutwialypiernik.flixage.repository.TrackStreamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service for counting track streams
 */
@Service
public class TrackStreamService {

    private final TrackStreamRepository repository;

    private final UserService userService;

    private final TrackService trackService;

    private final Clock clock;

    public TrackStreamService(TrackStreamRepository repository, UserService userService, TrackService trackService, Clock clock) {
        this.repository = repository;
        this.userService = userService;
        this.trackService = trackService;
        this.clock = clock;
    }

    @Transactional
    public void increaseStreamCount(String userId, String trackId) {
        final var user = userService.findById(userId).orElseThrow(() -> new AuthenticationException("Invalid Token"));
        final var track = trackService.findById(trackId).orElseThrow(ResourceNotFoundException::new);

        if (track.getAudioFile() == null) {
            throw new ResourceNotFoundException();
        }

        if (user.getLastAudioStream() != null && clock.instant().isBefore(user.getLastAudioStream().plusSeconds(30))) {
            throw new ConflictException("User has already streamed audio in last 30 seconds");
        }

        final var id = new TrackStreamId(user, track);
        final var stream = repository.findById(id).orElseGet(() -> {
            TrackStream newStream = new TrackStream();
            newStream.setId(id);

            return newStream;
        });

        stream.setStreamCount(stream.getStreamCount() + 1);

        repository.save(stream);
    }

    /**
     *
     * @return most recent streamed tracks
     */
    public Page<TrackStream> findRecentlyStreamedTracks(String userId, int offset, int limit) {
        return repository.findByIdUserIdOrderByUpdateTimeDesc(userId, PageRequest.of(offset / limit, limit));
    }

    /**
     *
     * @return most streamed artists within last 30 days
     */
    public Page<Artist> findMostStreamedArtists(String userId, int offset, int limit) {
        Instant now = clock.instant();

        return repository.findMostListenedArtists(userId, now, now.plus(30, ChronoUnit.DAYS), PageRequest.of(offset / limit, limit));
    }

}
