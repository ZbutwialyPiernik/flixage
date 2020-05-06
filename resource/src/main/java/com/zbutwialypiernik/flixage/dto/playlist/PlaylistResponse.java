package com.zbutwialypiernik.flixage.dto.playlist;

import com.zbutwialypiernik.flixage.dto.QueryableResponse;
import com.zbutwialypiernik.flixage.dto.UserResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlaylistResponse extends QueryableResponse {

    private UserResponse owner;

}
