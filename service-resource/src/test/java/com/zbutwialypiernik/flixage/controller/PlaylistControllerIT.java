package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.playlist.PlaylistRequest;
import com.zbutwialypiernik.flixage.repository.PlaylistRepository;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import org.apache.http.client.methods.RequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PlaylistControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlaylistService playlistService;

    @BeforeEach
    public void clearBeforeEach() {
        playlistService.deleteAll();
    }

    @Test
    public void playlistGetsCreated() throws Exception {
        var request = new PlaylistRequest("My Playlist");

        mockMvc.perform(post("/playlists"))
                .andExpect(status().isCreated());
    }

}
