package com.zbutwialypiernik.flixage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
public class PageResponse<T> {

    private final Collection<T> items;

    private final long total;

    public static <T> PageResponse<T>of(Stream<T> stream) {
        var items = stream.collect(Collectors.toList());

        return new PageResponse<>(items,  items.size());
    }

    public static <T> PageResponse<T>of(Collection<T> items) {
        return new PageResponse<>(items,  items.size());
    }

    public static <T> PageResponse<T>of(Page<T> items) {
        return new PageResponse<>(items.toList(), items.getTotalElements());
    }

}
