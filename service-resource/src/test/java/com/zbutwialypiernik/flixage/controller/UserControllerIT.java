package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT extends IntegrationTestWithPrincipal {

    @Autowired
    PlaylistService playlistService;

    @Test
    @Transactional
    void should_follow_playlist_if_user_is_logged_and_playlist_does_exists_and_user_is_not_owner() {
        var playlist = new Playlist();
        playlist.setName("playlist");
        playlist.setOwner(otherUser);
        playlist = playlistService.create(playlist);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, TOKEN_HEADER)
            .contentType(ContentType.JSON)
        .when()
            .put("/users/me/followedPlaylists/" + playlist.getShareCode())
        .then()
            .status(HttpStatus.OK);
        // @formatter:on

        final var playlists = userService.findById(user.getId()).get().getObservedPlaylists();

        Assertions.assertEquals( 1, playlists.size());
        Assertions.assertTrue(playlists.contains(playlist));
    }

    @Test
    @Transactional
    void should_not_follow_playlist_if_user_is_logged_and_playlist_does_not_exists() {
        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, TOKEN_HEADER)
            .contentType(ContentType.JSON)
        .when()
            .put("/users/me/followedPlaylists/" + UUID.randomUUID())
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on

        Assertions.assertEquals( 0, userService.findById(user.getId()).get().getObservedPlaylists().size());
    }

    @Test
    @Transactional
    void should_not_follow_playlist_if_user_has_no_authentication() {
        var playlist = new Playlist();
        playlist.setName("playlist");
        playlist.setOwner(user);
        playlist = playlistService.create(playlist);

        // @formatter:off
        given()
            .contentType(ContentType.JSON)
        .when()
            .put("/users/me/followedPlaylists/" + playlist.getShareCode())
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on

        Assertions.assertEquals( 0, userService.findById(user.getId()).get().getObservedPlaylists().size());
    }

    @Test
    @Transactional
    void should_not_follow_playlist_if_user_is_owner_of_playlist() {
        var playlist = new Playlist();
        playlist.setName("playlist");
        playlist.setOwner(user);
        playlist = playlistService.create(playlist);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, TOKEN_HEADER)
            .contentType(ContentType.JSON)
        .when()
            .put("/users/me/followedPlaylists/" + playlist.getShareCode())
        .then()
            .status(HttpStatus.BAD_REQUEST);
        // @formatter:on

        Assertions.assertEquals( 0, userService.findById(user.getId()).get().getObservedPlaylists().size());
    }

    @Test
    @Transactional
    void should_unfollow_playlist_if_user_has_observed_playlist() {
        var playlist = new Playlist();
        playlist.setName("playlist");
        playlist.setOwner(user);
        playlist = playlistService.create(playlist);

        user.getObservedPlaylists().add(playlist);
        userService.update(user);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, TOKEN_HEADER)
            .contentType(ContentType.JSON)
        .when()
            .delete("/users/me/followedPlaylists/" + playlist.getShareCode())
        .then()
            .status(HttpStatus.NO_CONTENT);
        // @formatter:on

        final var playlists = userService.findById(user.getId()).get().getObservedPlaylists();

        Assertions.assertEquals( 0, playlists.size());
        Assertions.assertFalse(playlists.contains(playlist));
    }
    
    @Test
    @Transactional
    void should_not_unfollow_playlist_if_user_has_not_observed_playlists() {
        var playlist = new Playlist();
        playlist.setName("playlist");
        playlist.setOwner(user);
        playlist = playlistService.create(playlist);

        user.getObservedPlaylists().add(playlist);
        userService.update(user);

        // @formatter:off
        given()
        .header(HttpHeaders.AUTHORIZATION, TOKEN_HEADER)
            .contentType(ContentType.JSON)
        .when()
            .delete("/users/me/followedPlaylists/" + playlist.getShareCode())
        .then()
            .status(HttpStatus.NO_CONTENT);
        // @formatter:on

        Assertions.assertEquals( 0, userService.findById(user.getId()).get().getObservedPlaylists().size());
    }

    @Test
    @Transactional
    void should_not_unfollow_playlist_if_playlist_has_different_shareCode() {
        var playlist = new Playlist();
        playlist.setName("playlist");
        playlist.setOwner(user);
        playlist = playlistService.create(playlist);

        user.getObservedPlaylists().add(playlist);
        userService.update(user);

        // @formatter:off
        given()
        .header(HttpHeaders.AUTHORIZATION, TOKEN_HEADER)
            .contentType(ContentType.JSON)
        .when()
            .delete("/users/me/followedPlaylists/" + UUID.randomUUID())
        .then()
            .status(HttpStatus.NOT_FOUND);
        // @formatter:on

        final var playlists = userService.findById(user.getId()).get().getObservedPlaylists();

        Assertions.assertEquals( 1, playlists.size());
        Assertions.assertTrue(playlists.contains(playlist));
    }

    @Test
    @Transactional
    void should_not_unfollow_playlist_if_user_is_not_logged() {
        var playlist = new Playlist();
        playlist.setName("playlist");
        playlist.setOwner(user);
        playlist = playlistService.create(playlist);

        user.getObservedPlaylists().add(playlist);
        userService.update(user);

       // @formatter:off
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete("/users/me/followedPlaylists/" + playlist.getId())
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on

        final var playlists = userService.findById(user.getId()).get().getObservedPlaylists();

        Assertions.assertEquals( 1, playlists.size());
        Assertions.assertTrue(playlists.contains(playlist));
    }

    @Test
    @Transactional
    void should_return_playlists_if_user_has_observed_playlists() {
        var playlist = new Playlist();
        playlist.setName("playlist");
        playlist.setOwner(user);
        playlist = playlistService.create(playlist);

        var playlist2 = new Playlist();
        playlist2.setName("playlist 2");
        playlist2.setOwner(user);
        playlist2 = playlistService.create(playlist2);

        user.getObservedPlaylists().add(playlist);
        user.getObservedPlaylists().add(playlist2);
        userService.update(user);

        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, TOKEN_HEADER)
            .contentType(ContentType.JSON)
        .when()
            .get("/users/me/followedPlaylists")
        .then()
            .status(HttpStatus.OK);
        // @formatter:on
    }

    @Test
    @Transactional
    void should_return_empty_list_if_user_has_not_observed_playlists() {
        // @formatter:off
        given()
            .header(HttpHeaders.AUTHORIZATION, TOKEN_HEADER)
            .contentType(ContentType.JSON)
        .when()
            .get("/users/me/followedPlaylists")
        .then()
            .status(HttpStatus.OK);
        // @formatter:on
    }

    @Test
    @Transactional
    void should_not_return_playlists_if_user_has_no_authentication() {
        // @formatter:off
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/users/me/followedPlaylists")
        .then()
            .status(HttpStatus.FORBIDDEN);
        // @formatter:on
    }

}
