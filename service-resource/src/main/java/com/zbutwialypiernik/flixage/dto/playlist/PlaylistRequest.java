package com.zbutwialypiernik.flixage.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PlaylistRequest {

    @NotNull(message = "Length cannot be null")
    @Length(min = 1, max = 32, message = "Length of name should be between 1 and 32")
    String name;

}
