package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.IntegrationTestWithPrincipal;
import com.zbutwialypiernik.flixage.dto.playlist.IdsRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistRequest;
import com.zbutwialypiernik.flixage.entity.Artist;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.Track;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationFilter;
import com.zbutwialypiernik.flixage.repository.PlaylistRepository;
import com.zbutwialypiernik.flixage.service.ArtistService;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.TrackService;
import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PlaylistControllerIT extends IntegrationTestWithPrincipal {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private TrackService trackService;

    //------------------------------- RETRIEVING PLAYLIST --------------------------------

    @Test
    public void logged_user_is_able_to_retrieve_playlist_when_playlist_exists() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .post("/playlists/" + playlist.getId())
        .then()
            .status(HttpStatus.CREATED)
            .body("id", equalTo(playlist.getId()))
            .body("name", equalTo(playlist.getName()));
        // @formatter:on
    }

    @Test
    public void logged_user_is_not_able_to_retrieve_playlist_when_playlist_does_not_exists() {
        var id = UUID.randomUUID();

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .get("/playlists/" + id)
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on
    }

    //------------------------------- CREATING PLAYLIST --------------------------------

    @Test
    public void logged_user_is_able_to_create_playlist() {
        var request = new PlaylistRequest("My Playlist");

        // @formatter:off
        String id =
                given()
                    .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
                    .contentType(ContentType.JSON)
                    .body(request).when()
                    .post("/playlists")
                .then()
                    .status(HttpStatus.CREATED)
                    .body("id", notNullValue())
                    .body("name", equalTo(request.getName()))
                .extract()
                    .path("id");
        // @formatter:on

        var playlist = playlistRepository.getOne(id);

        assertNotNull(playlist);
        assertEquals(playlist.getOwner(), user);
    }

    @Test
    public void not_logged_user_is_not_able_to_create_playlist() {
        var request = new PlaylistRequest("My Playlist");

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(request).when()
            .post("/playlists")
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on
    }

    //------------------------------- UPDATING TRACK FROM PLAYLIST --------------------------------

    @Test
    public void owner_is_able_to_update_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        var request = new PlaylistRequest("New playlist name");

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/playlists/" + playlist.getId())
        .then()
            .status(HttpStatus.OK)
            .body("id", notNullValue())
            .body("name", equalTo(request.getName()));
        // @formatter:on
    }

    @Test
    public void not_owner_is_not_able_to_update_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(otherUser);

        playlistService.create(playlist);

        var request = new PlaylistRequest("New playlist name");

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/playlists/" + playlist.getId())
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on

        var notUpdatedPlaylist = playlistRepository.getOne(playlist.getId());

        assertEquals(playlist.getId(), notUpdatedPlaylist.getId());
        assertEquals(playlist.getName(), notUpdatedPlaylist.getName());
        assertEquals(playlist.getOwner(), notUpdatedPlaylist.getOwner());
    }

    @Test
    public void logged_user_is_not_able_to_update_non_existing_playlist() {
        var request = new PlaylistRequest("New playlist name");

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/playlists/" + UUID.randomUUID())
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on
    }

    @Test
    public void not_logged_user_is_not_able_to_update_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        var request = new PlaylistRequest("New playlist name");

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/playlists/" + UUID.randomUUID())
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on
    }

    //------------------------------- REMOVING PLAYLIST --------------------------------

    @Test
    public void owner_is_able_to_delete_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .delete("/playlists/" + playlist.getId())
            .then()
            .status(HttpStatus.NO_CONTENT);
        // @formatter:on

        assertFalse(playlistRepository.existsById(playlist.getId()));
    }

    @Test
    public void not_owner_is_not_able_to_delete_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        // @formatter:off
        given()
                .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .delete("/playlists/" + playlist.getId())
                .then()
                .status(HttpStatus.NO_CONTENT);
        // @formatter:on

        assertFalse(playlistRepository.existsById(playlist.getId()));
    }

    @Test
    public void logged_user_is_not_able_to_delete_non_existing_playlist() {
        // @formatter:off
        given()
                .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .delete("/playlists/" + UUID.randomUUID().toString())
                .then()
                .status(HttpStatus.NOT_FOUND);
        // @formatter:on
    }

    @Test
    public void not_logged_user_is_not_able_to_delete_existing_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete("/playlists/" + UUID.randomUUID().toString())
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on
    }

    //------------------------------- ADDING TRACK FROM PLAYLIST --------------------------------

    @Test
    @Transactional
    public void owner_is_able_to_add_existing_tracks_to_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        var track1 = new Track();
        track1.setName("Bohemian Rhapsody");

        trackService.create(track1);

        var track2 = new Track();
        track2.setName("Don't Stop Me Now");

        trackService.create(track2);

        var request = new IdsRequest(Set.of(track1.getId(), track2.getId()));

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/playlists/" + playlist.getId() + "/tracks")
        .then()
            .status(HttpStatus.NO_CONTENT);
        // @formatter:on

        var tracks = playlistRepository.getOne(playlist.getId()).getTracks();

        assertEquals(2, tracks.size());
    }

    @Test
    @Transactional
    public void owner_is_not_able_to_add_non_existing_tracks_to_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        var track1 = new Track();
        track1.setName("Bohemian Rhapsody");

        trackService.create(track1);

        var request = new IdsRequest(Set.of(track1.getId(), UUID.randomUUID().toString()));

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/playlists/" + playlist.getId() + "/tracks")
        .then()
            .status(HttpStatus.BAD_REQUEST);
        // @formatter:on

        var tracks = playlistRepository.getOne(playlist.getId()).getTracks();

        assertEquals(0, tracks.size());
    }

    @Test
    @Transactional
    public void not_owner_is_able_to_add_existing_tracks_to_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(otherUser);

        playlistService.create(playlist);

        var track1 = new Track();
        track1.setName("Bohemian Rhapsody");

        trackService.create(track1);

        var track2 = new Track();
        track2.setName("Don't Stop Me Now");

        trackService.create(track2);

        var request = new IdsRequest(Set.of(track1.getId(), track2.getId()));

        // @formatter:off
        given()
                .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/playlists/" + playlist.getId() + "/tracks")
                .then()
                .status(HttpStatus.FORBIDDEN);
        // @formatter:on

        var tracks = playlistRepository.getOne(playlist.getId()).getTracks();

        assertEquals(0, tracks.size());
    }

    //------------------------------- REMOVING TRACK FROM PLAYLIST --------------------------------

    @Test
    @Transactional
    public void owner_is_able_to_remove_tracks_from_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        var track1 = new Track();
        track1.setName("Bohemian Rhapsody");

        var track2 = new Track();
        track2.setName("Don't Stop Me Now");

        trackService.create(track1);
        trackService.create(track2);

        playlist.getTracks().add(track1);
        playlist.getTracks().add(track2);

        playlistService.update(playlist);

        var request = new IdsRequest(Set.of(track1.getId(), track2.getId()));

        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .delete("/playlists/" + playlist.getId() + "/tracks")
        .then()
            .status(HttpStatus.NO_CONTENT);
        // @formatter:on

        var tracks = playlistRepository.getOne(playlist.getId()).getTracks();

        assertEquals(0, tracks.size());
    }

    @Test
    @Transactional
    public void not_owner_is_able_to_remove_tracks_from_playlist() {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(otherUser);

        playlistService.create(playlist);

        var track1 = new Track();
        track1.setName("Bohemian Rhapsody");

        var track2 = new Track();
        track2.setName("Don't Stop Me Now");

        trackService.create(track1);
        trackService.create(track2);

        playlist.getTracks().add(track1);
        playlist.getTracks().add(track2);

        playlistService.update(playlist);

        var request = new IdsRequest(Set.of(track1.getId(), track2.getId()));

        // @formatter:off
        given()
                .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .delete("/playlists/" + playlist.getId() + "/tracks")
                .then()
                .status(HttpStatus.FORBIDDEN);
        // @formatter:on

        var tracks = playlistRepository.getOne(playlist.getId()).getTracks();

        assertEquals(2, tracks.size());
    }

    //------------------------------- THUMBNAIL UPLOAD --------------------------------

    @Test
    @Transactional
    public void logged_user_is_able_to_upload_thumbnail_to_playlist() throws URISyntaxException {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        var thumbnailFile = new File(ClassLoader.getSystemResource("thumbnail.png").toURI());
        
        // @formatter:off
        given()
            .header("Authorization", JwtAuthenticationFilter.TOKEN_PREFIX + TOKEN)
            .contentType(ContentType.JSON)
            .multiPart(thumbnailFile)
        .when()
            .post("/playlists/" + playlist.getId() + "/thumbnail")
        .then()
            .status(HttpStatus.OK);
        // @formatter:on

        var thumbnail = playlistRepository.getOne(playlist.getId()).getThumbnail();

        assertNotNull(thumbnail);
    }

    // RestAssured does not support MockMultipartFile
    @Test
    @Transactional
    public void cannot_upload_thumbnail_when_user_is_not_owner() throws Exception {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(otherUser);

        playlistService.create(playlist);

        var mockMultipartFile = new MockMultipartFile("file","thumbnail.png", MediaType.IMAGE_JPEG_VALUE,
                new byte[(int) (ImageResource.MAX_FILE_SIZE - FileUtils.ONE_MB)]);

        mockMvc.perform(
                multipart("/playlists/" + playlist.getId() + "/thumbnail")
                        .file(mockMultipartFile)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isForbidden());

        var thumbnail = playlistRepository.getOne(playlist.getId()).getThumbnail();

        assertNull(thumbnail);
    }

    // RestAssured does not support MockMultipartFile
    @Test
    @Transactional
    public void cannot_upload_thumbnail_when_image_is_too_big() throws Exception {
        var playlist = new Playlist();
        playlist.setName("Playlist name");
        playlist.setOwner(user);

        playlistService.create(playlist);

        // File bigger than current limit
        var mockMultipartFile = new MockMultipartFile("file","thumbnail.png", MediaType.IMAGE_JPEG_VALUE,
                new byte[(int) (ImageResource.MAX_FILE_SIZE + FileUtils.ONE_MB)]);

        mockMvc.perform(
                multipart("/playlists/" + playlist.getId() + "/thumbnail")
                        .file(mockMultipartFile)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isPayloadTooLarge());

        var thumbnail = playlistRepository.getOne(playlist.getId()).getThumbnail();

        assertNull(thumbnail);
    }

}
