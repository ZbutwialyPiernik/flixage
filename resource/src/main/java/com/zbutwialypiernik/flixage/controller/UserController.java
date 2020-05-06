package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.UserResponse;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.filter.JwtAuthenticationToken;
import com.zbutwialypiernik.flixage.repository.ThumbnailStore;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.UserService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController extends QueryableController<User, UserResponse> {
    
    private final PlaylistService playlistService;

    private final UserService userService;

    private final MapperFacade mapper;

    public UserController(UserService userService, PlaylistService playlistService, ThumbnailStore thumbnailStore, MapperFacade mapper) {
        super(userService);
        this.playlistService = playlistService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal JwtAuthenticationToken principal) {
        return toResponse(userService.findById(principal.getId()));
    }

    @GetMapping("/me/playlists")
    public List<PlaylistResponse> getCurrentUserPlaylist(@AuthenticationPrincipal JwtAuthenticationToken principal) {
        return getUserPlaylists(principal.getId());
    }

    @GetMapping("/{id}/playlists")
    public List<PlaylistResponse> getUserPlaylists(@PathVariable String id) {
        return playlistService.findByUserId(id).stream()
                .map(playlist -> mapper.map(playlist, PlaylistResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse toResponse(User user) {
        return mapper.map(user, UserResponse.class);
    }

}
