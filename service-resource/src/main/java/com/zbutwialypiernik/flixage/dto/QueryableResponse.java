package com.zbutwialypiernik.flixage.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class QueryableResponse {

    private String id;

    private String name;

    private String thumbnailUrl;

}
