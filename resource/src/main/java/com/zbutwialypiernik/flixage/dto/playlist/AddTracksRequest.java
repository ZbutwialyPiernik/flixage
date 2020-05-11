package com.zbutwialypiernik.flixage.dto.playlist;

import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class AddTracksRequest {

    List<String> ids;

}
