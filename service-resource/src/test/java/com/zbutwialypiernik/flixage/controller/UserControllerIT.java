package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT extends IntegrationTestWithPrincipal {

    @Autowired
    PlaylistService playlistService;

    //------------------------------- GETTING FOLLOWED PLAYLISTS --------------------------------

    @Test
    @Transactional
    void should_return_playlists_if_user_has_observed_playlists() {
        var playlist = new Playlist();
        playlist.setName("playlist");
        playlist.setOwner(otherUser);
        playlist.getFollowers().add(user);
        playlist = playlistService.create(playlist);

        var playlist2 = new Playlist();
        playlist2.setName("playlist 2");
        playlist2.setOwner(otherUser);
        playlist2.getFollowers().add(user);
        playlist2 = playlistService.create(playlist2);

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
