package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.UserResponse;
import com.zbutwialypiernik.flixage.entity.Playlist;
import com.zbutwialypiernik.flixage.entity.User;
import com.zbutwialypiernik.flixage.filter.JwtUserPrincipal;
import com.zbutwialypiernik.flixage.repository.ThumbnailStore;
import com.zbutwialypiernik.flixage.service.PlaylistService;
import com.zbutwialypiernik.flixage.service.UserService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends QueryableController<User, UserResponse> {
    
    private final PlaylistService playlistService;

    private final UserService userService;

    private final MapperFacade mapperFacade;

    public UserController(UserService userService, PlaylistService playlistService, ThumbnailStore<User> thumbnailStore, MapperFacade mapperFacade) {
        super(userService);
        this.playlistService = playlistService;
        this.mapperFacade = mapperFacade;
        this.userService = userService;
    }

    @GetMapping("/me/playlists")
    public List<Playlist> getCurrentUserPlaylist(@AuthenticationPrincipal JwtUserPrincipal principal) {
        return playlistService.findByUserId(principal.getId());
    }

    @GetMapping("/{id}/playlists")
    public List<Playlist> getUserPlaylists(String id) {
        return playlistService.findByUserId(id);
    }

    @Override
    public UserResponse toResponse(User user) {
        return mapperFacade.map(user, UserResponse.class);
    }

}
