package com.zbutwialypiernik.flixage.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IdsRequest {

    @NotNull(message = "Parameter ids is required")
    @NotEmpty(message = "Parameter ids cannot be empty")
    Set<String> ids;

}
