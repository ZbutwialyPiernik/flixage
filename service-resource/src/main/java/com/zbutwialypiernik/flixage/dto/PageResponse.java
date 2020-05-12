package com.zbutwialypiernik.flixage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
public class PageResponse<T> {

    private final Collection<T> items;

    private final long total;

}
