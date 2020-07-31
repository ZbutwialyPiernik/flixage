package com.zbutwialypiernik.flixage.controller;

import com.zbutwialypiernik.flixage.dto.AlbumResponse;
import com.zbutwialypiernik.flixage.dto.PageResponse;
import com.zbutwialypiernik.flixage.entity.Album;
import com.zbutwialypiernik.flixage.service.AlbumService;
import com.zbutwialypiernik.flixage.service.QueryableService;
import ma.glasnost.orika.MapperFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/albums"))
public class AlbumController extends QueryableController<Album, AlbumResponse>  {

    public AlbumController(QueryableService<Album> service, MapperFactory mapperFactory) {
        super(service, mapperFactory);
    }

    @GetMapping("/recent")
    public PageResponse<AlbumResponse> getRecentlyAddedTrack(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit) {
        final var page = getService().getRecentlyAdded(offset, limit);

        return new PageResponse<>(page
                .map(dtoMapper::map)
                .toList(), page.getTotalElements());
    }

    @Override
    public AlbumService getService() {
        return (AlbumService) super.getService();
    }

}
