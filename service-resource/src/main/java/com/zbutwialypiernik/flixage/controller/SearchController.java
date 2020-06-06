package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.config.mapping.CaseInsensitiveEnumEditor;
import com.zbutwialypiernik.flixage.dto.*;
import com.zbutwialypiernik.flixage.dto.playlist.PlaylistResponse;
import com.zbutwialypiernik.flixage.entity.*;
import com.zbutwialypiernik.flixage.exception.BadRequestException;
import com.zbutwialypiernik.flixage.service.*;
import com.zbutwialypiernik.flixage.util.StringUtils;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

// TODO: elastic search or something similar
@RestController
public class SearchController {

    private final TrackService trackService;
    private final AlbumService albumService;
    private final ArtistService artistService;
    private final UserService userService;
    private final PlaylistService playlistService;

    private final BoundMapperFacade<Track, TrackResponse> trackMapper;
    private final BoundMapperFacade<Artist, ArtistResponse> artistMapper;
    private final BoundMapperFacade<Album, AlbumResponse> albumMapper;
    private final BoundMapperFacade<User, UserResponse> userMapper;
    private final BoundMapperFacade<Playlist, PlaylistResponse> playlistMapper;

    public SearchController(TrackService trackService, AlbumService albumService, ArtistService artistService, UserService userService, PlaylistService playlistService, MapperFactory mapperFactory) {
        this.trackService = trackService;
        this.albumService = albumService;
        this.artistService = artistService;
        this.userService = userService;
        this.playlistService = playlistService;

        trackMapper = mapperFactory.getMapperFacade(Track.class, TrackResponse.class);
        artistMapper = mapperFactory.getMapperFacade(Artist.class, ArtistResponse.class);
        albumMapper = mapperFactory.getMapperFacade(Album.class, AlbumResponse.class);
        userMapper = mapperFactory.getMapperFacade(User.class, UserResponse.class);
        playlistMapper = mapperFactory.getMapperFacade(Playlist.class, PlaylistResponse.class);
    }

    @GetMapping("/search")
    public SearchResponse searchByName(@RequestParam String query, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit, @RequestParam Set<QueryableType> type) {
        if (StringUtils.isBlank(query)) {
            throw new BadRequestException("Param 'query' cannot be blank");
        }

        if (offset < 0) {
            throw new BadRequestException("Offset cannot be negative");
        }

        if (limit <= 0) {
            throw new BadRequestException("Limit should be positive");
        }

        if (type.isEmpty()) {
            throw new BadRequestException("Type not included");
        }

        SearchResponse searchResponse = new SearchResponse();

        if (type.contains(QueryableType.TRACK)) {
            Page<Track> page = trackService.findByName(query, offset, limit);

            searchResponse.setTracks(new PageResponse<>(page.getContent()
                    .stream()
                    .map(trackMapper::map)
                    .collect(Collectors.toList()), page.getTotalElements()));
        }

        if (type.contains(QueryableType.ALBUM)) {
            Page<Album> page = albumService.findByName(query, offset, limit);

            searchResponse.setAlbums(new PageResponse<>(page.getContent()
                    .stream()
                    .map(albumMapper::map)
                    .collect(Collectors.toList()), page.getTotalElements()));
        }

        if (type.contains(QueryableType.ARTIST)) {
            Page<Artist> page = artistService.findByName(query, offset, limit);

            searchResponse.setArtists(new PageResponse<>(page.getContent()
                    .stream()
                    .map(artistMapper::map)
                    .collect(Collectors.toList()), page.getTotalElements()));
        }

        if (type.contains(QueryableType.USER)) {
            Page<User> page = userService.findByName(query, offset, limit);

            searchResponse.setUsers(new PageResponse<>(page.getContent()
                    .stream()
                    .map(userMapper::map)
                    .collect(Collectors.toList()), page.getTotalElements()));
        }

        if (type.contains(QueryableType.PLAYLIST)) {
            Page<Playlist> page = playlistService.findByName(query, offset, limit);

            searchResponse.setPlaylists(new PageResponse<>(page.getContent()
                    .stream()
                    .map(playlistMapper::map)
                    .collect(Collectors.toList()), page.getTotalElements()));
        }

        return searchResponse;
    }

    @InitBinder
    public void binder(WebDataBinder binder) {
        binder.registerCustomEditor(QueryableType.class, new CaseInsensitiveEnumEditor<>(QueryableType.class));
    }

}
