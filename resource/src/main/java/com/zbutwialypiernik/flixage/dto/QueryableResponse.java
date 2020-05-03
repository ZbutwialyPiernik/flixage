package com.zbutwialypiernik.flixage.dto;

import lombok.Data;

@Data
public abstract class QueryableResponse {

    private String id;

    private String name;

    private String thumbnailUrl;

}
