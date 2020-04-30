package com.zbutwialypiernik.flixage.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
public class PageResponse<T> {

    private final Collection<T> items;

    private final long total;

}
