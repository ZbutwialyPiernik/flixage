package com.zbutwialypiernik.flixage.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PlaylistRequest {

    String name;

}
