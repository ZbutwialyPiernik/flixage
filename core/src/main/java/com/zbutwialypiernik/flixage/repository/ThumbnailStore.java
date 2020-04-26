package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Queryable;
import org.springframework.content.commons.repository.ContentStore;

public interface ThumbnailStore<T extends Queryable> extends ContentStore<T, byte[]> {

}
