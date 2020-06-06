package com.zbutwialypiernik.flixage.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AddTracksRequest {

    Set<String> ids;

}
