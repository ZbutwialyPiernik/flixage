package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.TestWithPrincipal;
import com.zbutwialypiernik.flixage.config.GatewayUriBuilder;
import com.zbutwialypiernik.flixage.config.MapperConfiguration;
import com.zbutwialypiernik.flixage.config.WebSecurityConfig;
import com.zbutwialypiernik.flixage.dto.mapper.DtoMappersConfiguration;
import com.zbutwialypiernik.flixage.dto.mapper.converter.CustomConverterConfiguration;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistRequest;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.exception.handler.DefaultExceptionHandler;
import com.zbutwialypiernik.flixage.repository.PlaylistRepository;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.UserService;
import io.restassured.http.ContentType;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

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
    @WithMockUser
    public void playlist_gets_created_when_has_valid_authentication() throws Exception {
        var request = new PlaylistRequest("My Playlist");
        Playlist createdPlaylist = new Playlist();
        createdPlaylist.setName("My Playlist");

        when(userService.findById(token.getId())).thenReturn(Optional.of(user));
        when(playlistService.create(Mockito.any())).thenReturn(createdPlaylist);

        given().header("Authorization", "Bearer token")
                .contentType(ContentType.JSON)
                .body(request).when()
                .post("/playlists")
                .then()
                .log().all()
                .status(HttpStatus.CREATED)
                .body("name", equalTo("My Playlist"));
    }

    // Can occur when JWT token is valid, but user account got deleted
    @Test
    public void playlist_gets_created_has_invalid_authentication() {
        var request = new PlaylistRequest("My Playlist");

        when(userService.findById(token.getId())).thenReturn(Optional.empty());

        given().header("Authorization", "Bearer token")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/playlists")
                .then()
                .status(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void playlist_gets_updated_when_user_is_logged() {
        final var request = new PlaylistRequest("My New Playlist Name");
        final var playlistId = "0000-0000-0000-0000";

        Playlist playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setOwner(user);
        playlist.setName("My Old Playlist Name");
        playlist.setOwner(user);

        Playlist newPlaylist = new Playlist();
        newPlaylist.setId(playlistId);
        newPlaylist.setOwner(user);
        newPlaylist.setName("My New Playlist Name");
        newPlaylist.setOwner(user);

        when(playlistService.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(playlistService.update(Mockito.any())).thenReturn(newPlaylist);

        given().header("Authorization", "Bearer token")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/playlists/" + playlistId)
                .then()
                .status(HttpStatus.OK)
                .body("name", equalTo("My New Playlist Name"));
    }

    @Test
    public void playlist_does_not_get_updated_when_does_not_exists() {
        final var request = new PlaylistRequest("My New Playlist Name");
        final var playlistId = "0000-0000-0000-0000";

        when(playlistService.findById(playlistId)).thenReturn(Optional.empty());

        given().header("Authorization", "Bearer token")
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/playlists/" + playlistId)
                .then()
                .status(HttpStatus.NOT_FOUND);
    }

}
