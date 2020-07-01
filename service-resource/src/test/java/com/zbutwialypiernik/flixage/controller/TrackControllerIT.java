package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.IntegrationTestWithPrincipal;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.entity.file.AudioFileEntity;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationFilter;
import com.zbutwialypiernik.flixage.repository.TrackRepository;
import com.zbutwialypiernik.flixage.service.TrackService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {TrackControllerIT.TimeZoneConfig.class})
public class TrackControllerIT extends IntegrationTestWithPrincipal {

    public static final Instant NOW = Instant.now();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private TrackService trackService;

    @TestConfiguration
    public static class TimeZoneConfig {

        @Bean
        @Primary
        public Clock clock() {
            System.out.println("CUSTOM CLOCK");

            return Clock.fixed(NOW, ZoneId.of("UTC"));
        }

    }

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

    @BeforeEach
    public void setup() {
        mockMvc(mockMvc);
    }

    @Test
    @Transactional
    public void logged_user_is_able_to_increase_track_stream_count_when_track_exists_and_has_audio_file() {
        var audioFile = createMockFile();

        var track = new Track();
        track.setName("Playlist name");
        track.setAudioFile(audioFile);

        user.setLastAudioStream(NOW.minus(Duration.ofSeconds(35)));
        userService.update(user);

        trackService.create(track);

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .post("/tracks/" + track.getId() + "/streamCount")
        .then()
            .status(HttpStatus.OK);
        // @formatter:on

        assertEquals(1, trackRepository.getOne(track.getId()).getStreamCount());
    }

    @Test
    @Transactional
    public void logged_user_is_not_able_to_increase_track_stream_count_when_track_exists_and_has_not_audio_file() {
        var track = new Track();
        track.setName("Playlist name");
        track.setAudioFile(null);

        trackService.create(track);

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .post("/tracks/" + track.getId() + "/streamCount")
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on

        assertEquals(0, trackRepository.getOne(track.getId()).getStreamCount());
    }

    @Test
    @Transactional
    public void logged_user_is_not_able_to_increase_track_stream_count_when_request_is_sent_before_cooldown() {
        var audioFile = createMockFile();

        var track = new Track();
        track.setName("Playlist name");
        track.setAudioFile(audioFile);

        user.setLastAudioStream(NOW.minus(Duration.ofSeconds(27)));
        userService.update(user);

        trackService.create(track);

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .post("/tracks/" + track.getId() + "/streamCount")
        .then()
            .status(HttpStatus.CONFLICT);
        // @formatter:on

        assertEquals(0, trackRepository.getOne(track.getId()).getStreamCount());
    }

    @Test
    @Transactional
    public void not_logged_user_is_not_able_to_increase_track_stream_count() {
        var audioFile = createMockFile();

        var track = new Track();
        track.setName("Playlist name");
        track.setAudioFile(audioFile);

        trackService.create(track);

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/tracks/" + track.getId() + "/streamCount")
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on

        assertEquals(0, trackRepository.getOne(track.getId()).getStreamCount());
    }

}
