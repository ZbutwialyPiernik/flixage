package com.zbutwialypiernik.flixage.dto.playlist;

import lombok.Data;

import java.util.List;

@Data
public class AddTracksRequest {

    private List<String> ids;

}
