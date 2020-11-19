package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.TestWithPrincipal;
import com.zbutwialypiernik.flixage.dto.playlist.IdsRequest;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistRequest;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.repository.PlaylistRepository;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.UserService;
import com.zbutwialypiernik.flixage.service.resource.image.ImageResource;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlaylistController.class)
public class PlaylistControllerTest extends TestWithPrincipal {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlaylistRepository playlistRepository;

    @MockBean
    private PlaylistService playlistService;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
        mockMvc(mockMvc);
    }

    @Test
    public void playlist_gets_created_when_has_valid_authentication() {
        var request = new PlaylistRequest("My Playlist");
        var createdPlaylist = new Playlist();
        createdPlaylist.setId(UUID.randomUUID().toString());
        createdPlaylist.setName("My Playlist");

        when(userService.findById(any())).thenReturn(Optional.of(user));
        when(playlistService.create(any())).thenReturn(createdPlaylist);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/playlists")
        .then()
            .status(HttpStatus.CREATED)
            .body("id", equalTo(createdPlaylist.getId()))
            .body("name", equalTo("My Playlist"));
        // @formatter:on
    }

    // Can occur when JWT token is valid, but user account got deleted
    @Test
    public void playlist_gets_created_has_invalid_authentication() {
        var request = new PlaylistRequest("My Playlist");

        when(userService.findById(any())).thenReturn(Optional.empty());

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/playlists")
        .then()
            .status(HttpStatus.UNAUTHORIZED);
        // @formatter:on
    }

    @Test
    public void playlist_gets_updated_when_user_is_owner() {
        final var request = new PlaylistRequest("My New Playlist Name");
        final var playlistId = "0000-0000-0000-0000";

        var playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setOwner(user);
        playlist.setName("My Old Playlist Name");

        var newPlaylist = new Playlist();
        newPlaylist.setId(playlistId);
        newPlaylist.setOwner(user);
        newPlaylist.setName("My New Playlist Name");

        when(playlistService.findById(any())).thenReturn(Optional.of(playlist));
        when(playlistService.update(any())).thenReturn(newPlaylist);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/playlists/" + playlistId)
        .then()
            .status(HttpStatus.OK)
            .body("id", equalTo(playlistId))
            .body("name", equalTo("My New Playlist Name"));
        // @formatter:on
    }

    @Test
    public void playlist_does_not_get_updated_when_does_not_exists() {
        final var request = new PlaylistRequest("My New Playlist Name");
        final var playlistId = "0000-0000-0000-0000";

        when(playlistService.findById(any())).thenReturn(Optional.empty());

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/playlists/" + playlistId)
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on
    }

    @Test
    public void playlist_does_not_get_updated_when_user_is_not_owner() {
        final var request = new PlaylistRequest("My New Playlist Name");
        final var playlistId = "0000-0000-0000-0000";

        var playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setOwner(otherUser);
        playlist.setName("My Old Playlist Name");

        when(playlistService.findById(any())).thenReturn(Optional.of(playlist));

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/playlists/" + playlistId)
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on
    }

    @Test
    public void playlist_gets_deleted_when_user_is_owner() {
        final var playlistId = "0000-0000-0000-0000";

        var playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setOwner(user);
        playlist.setName("My Old Playlist Name");

        when(playlistService.findById(any())).thenReturn(Optional.of(playlist));

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(ContentType.JSON)
        .when()
            .delete("/playlists/" + playlistId)
        .then()
            .status(HttpStatus.NO_CONTENT);
        // @formatter:on
    }

    @Test
    public void playlist_does_not_get_deleted_when_does_not_exists() {
        final var playlistId = "0000-0000-0000-0000";

        when(playlistService.findById(any())).thenReturn(Optional.empty());

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(ContentType.JSON)
        .when()
            .delete("/playlists/" + playlistId)
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on
    }

    @Test
    public void playlist_does_not_get_deleted_when_user_is_not_owner() {
        final var playlistId = "0000-0000-0000-0000";

        var playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setOwner(otherUser);
        playlist.setName("My Old Playlist Name");

        when(playlistService.findById(any())).thenReturn(Optional.of(playlist));

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(ContentType.JSON)
        .when()
            .delete("/playlists/" + playlistId)
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on
    }

    @Test
    public void can_add_track_to_playlist_when_user_is_owner() {
        final var request = new IdsRequest(Set.of("1234-1234-1234-1234"));

        var playlist = new Playlist();
        playlist.setId("0000-0000-0000-0000");
        playlist.setOwner(user);
        playlist.setName("My Old Playlist Name");

        when(playlistService.findById(any())).thenReturn(Optional.of(playlist));

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .body(request)
            .contentType(ContentType.JSON)
        .when()
            .put("/playlists/" + playlist.getId() + "/tracks")
        .then()
            .status(HttpStatus.OK);
        // @formatter:on
    }

    @Test
    public void cannot_add_track_to_playlist_when_track_ids_are_missing() {
        final var playlistId = "0000-0000-0000-0000";
        final var request = new IdsRequest(Collections.emptySet());

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .body(request)
            .contentType(ContentType.JSON)
        .when()
            .put("/playlists/" + playlistId + "/tracks")
        .then()
            .status(HttpStatus.BAD_REQUEST);
        // @formatter:on
    }

    @Test
    public void cannot_add_track_to_playlist_when_playlist_does_not_exists() {
        final var playlistId = "0000-0000-0000-0000";
        final var request = new IdsRequest(Set.of("1234-1234-1234-1234"));

        when(playlistService.findById(any())).thenReturn(Optional.empty());

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .body(request)
            .contentType(ContentType.JSON)
        .when()
            .put("/playlists/" + playlistId + "/tracks")
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on
    }

    @Test
    public void cannot_add_track_to_playlist_when_user_is_not_owner() {
        final var playlistId = "0000-0000-0000-0000";
        final var request = new IdsRequest(Set.of("1234-1234-1234-1234"));

        var playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setOwner(otherUser);
        playlist.setName("My Old Playlist Name");

        when(playlistService.findById(any())).thenReturn(Optional.of(playlist));

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .body(request)
            .contentType(ContentType.JSON)
        .when()
            .put("/playlists/" + playlistId + "/tracks")
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on
    }

    // RestAssured does not support MockMultipartFile
    @Test
    public void can_upload_thumbnail_when_user_is_owner() throws Exception {
        final var playlistId = "0000-0000-0000-0000";

        var mockMultipartFile = new MockMultipartFile("file","thumbnail.jpg", MediaType.IMAGE_JPEG_VALUE,
                new byte[(int) (ImageResource.MAX_FILE_SIZE - FileUtils.ONE_MB)]);

        var playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setOwner(user);
        playlist.setName("My Playlist Name");

        when(playlistService.findById(any())).thenReturn(Optional.of(playlist));

        mockMvc.perform(
                multipart("/playlists/" + playlistId + "/thumbnail")
                        .file(mockMultipartFile)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.IMAGE_JPEG))
                        .andExpect(status().isCreated());
    }

    // RestAssured does not support MockMultipartFile
    @Test
    public void cannot_upload_thumbnail_when_user_is_not_owner() throws Exception {
        final var playlistId = "0000-0000-0000-0000";

        var mockMultipartFile = new MockMultipartFile("file","thumbnail.jpg", MediaType.IMAGE_JPEG_VALUE,
                new byte[(int) (ImageResource.MAX_FILE_SIZE - FileUtils.ONE_MB)]);

        var playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setOwner(otherUser);
        playlist.setName("My Old Playlist Name");

        when(playlistService.findById(any())).thenReturn(Optional.of(playlist));

        mockMvc.perform(
                multipart("/playlists/" + playlistId + "/thumbnail")
                        .file(mockMultipartFile)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isForbidden());
    }
    
    // RestAssured does not support MockMultipartFile
    @Test
    public void cannot_upload_thumbnail_when_image_is_too_big() throws Exception {
        final var playlistId = "0000-0000-0000-0000";

        // File bigger than current limit
        var mockMultipartFile = new MockMultipartFile("file","thumbnail.jpg", MediaType.IMAGE_JPEG_VALUE,
                new byte[(int) (ImageResource.MAX_FILE_SIZE + FileUtils.ONE_MB)]);

        var playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setOwner(user);
        playlist.setName("My Playlist Name");

        when(playlistService.findById(any())).thenReturn(Optional.of(playlist));

        mockMvc.perform(
                multipart("/playlists/" + playlistId + "/thumbnail")
                        .file(mockMultipartFile)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isPayloadTooLarge());
    }

}
