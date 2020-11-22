package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.TrackStreamId;
import com.zbutwialypiernik.flixage.entity.file.AudioFileEntity;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationFilter;
import com.zbutwialypiernik.flixage.repository.TrackStreamRepository;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.service.TrackService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TrackControllerIT extends IntegrationTestWithPrincipal {

    @Autowired
    private TrackService trackService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private TrackStreamRepository streamRepository;

    private AudioFileEntity createMockFile() {
        var audioFile = new AudioFileEntity();
        audioFile.setId(UUID.randomUUID().toString());
        audioFile.setExtension("PNG");
        audioFile.setMimeType("image/png");
        audioFile.setSize(1234567);
        audioFile.setFileId("1234-1234-1234-1234");
        audioFile.setDuration(Duration.ofSeconds(1234));

        return audioFile;
    }

    //------------------------------- RETRIEVING TRACK --------------------------------

    @Test
    public void logged_user_is_able_to_retrieve_track_when_exists() {
        var audioFile = createMockFile();

        var artist = new Artist();
        artist.setName("Queen");

        artistService.create(artist);

        var track = new Track();
        track.setName("Track name");
        track.setAudioFile(audioFile);
        track.setArtist(artist);

        track = trackService.create(track);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .get("/tracks/" + track.getId())
        .then()
            .status(HttpStatus.OK)
            .body("id", equalTo(track.getId()))
            .body("name", equalTo(track.getName()));    // @formatter:on
    }

    @Test
    public void logged_user_is_not_able_to_retrieve_track_when_track_does_not_exists() {
        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .get("/tracks/" + UUID.randomUUID().toString())
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on
    }

    @Test
    public void logged_user_is_not_able_to_retrieve_track_when_audio_file_does_not_exists() {
        var artist = new Artist();
        artist.setName("Artist");

        artistService.create(artist);

        var track = new Track();
        track.setName("Playlist name");
        track.setAudioFile(null);
        track.setArtist(artist);

        user.setLastAudioStream(Instant.now());
        userService.update(user);

        track = trackService.create(track);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .get("/tracks/" + track.getId())
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on
    }

    @Test
    public void not_logged_user_is_not_able_to_retrieve_track_when_track_does_exists() {
        var audioFile = createMockFile();

        var artist = new Artist();
        artist.setName("Artist");

        artistService.create(artist);

        var track = new Track();
        track.setName("Playlist name");
        track.setAudioFile(audioFile);
        track.setArtist(artist);

        trackService.create(track);

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/tracks/" + UUID.randomUUID().toString())
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on
    }

    //------------------------------- INCREASING STREAM COUNT --------------------------------

    @Test
    @Transactional
    public void logged_user_is_able_to_increase_track_stream_count_when_track_exists_and_has_audio_file() {
        var audioFile = createMockFile();

        var artist = new Artist();
        artist.setName("Artist");

        artistService.create(artist);

        var track = new Track();
        track.setName("Track name");
        track.setAudioFile(audioFile);
        track.setArtist(artist);

        user.setLastAudioStream(Instant.now().minus(Duration.ofSeconds(35)));
        userService.update(user);

        track = trackService.create(track);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .post("/tracks/" + track.getId() + "/streamCount")
        .then()
            .status(HttpStatus.OK);
        // @formatter:on

        assertEquals(1, streamRepository.getOne(new TrackStreamId(user, track)).getStreamCount());
    }

    @Test
    @Transactional
    public void logged_user_is_not_able_to_increase_track_stream_count_when_track_exists_and_has_not_audio_file() {
        var artist = new Artist();
        artist.setName("Artist");

        artistService.create(artist);

        var track = new Track();
        track.setName("Playlist name");
        track.setAudioFile(null);
        track.setArtist(artist);

        track = trackService.create(track);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .post("/tracks/" + track.getId() + "/streamCount")
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on

        assertTrue(streamRepository.findById(new TrackStreamId(user, track)).isEmpty());
    }

    @Test
    @Transactional
    public void logged_user_is_not_able_to_increase_track_stream_count_when_request_is_sent_before_cooldown() {
        var audioFile = createMockFile();

        var artist = new Artist();
        artist.setName("Artist");

        artistService.create(artist);

        var track = new Track();
        track.setName("Playlist name");
        track.setAudioFile(audioFile);
        track.setArtist(artist);

        user.setLastAudioStream(Instant.now());
        userService.update(user);

        track = trackService.create(track);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .post("/tracks/" + track.getId() + "/streamCount")
        .then()
            .status(HttpStatus.CONFLICT);
        // @formatter:on

        assertTrue(streamRepository.findById(new TrackStreamId(user, track)).isEmpty());
    }

    @Test
    @Transactional
    public void not_logged_user_is_not_able_to_increase_track_stream_count() {
        var audioFile = createMockFile();

        var artist = new Artist();
        artist.setName("Artist");

        artistService.create(artist);

        var track = new Track();
        track.setName("Playlist name");
        track.setAudioFile(audioFile);
        track.setArtist(artist);

        trackService.create(track);

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/tracks/" + track.getId() + "/streamCount")
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on

        assertTrue(streamRepository.findById(new TrackStreamId(user, track)).isEmpty());
    }

}
