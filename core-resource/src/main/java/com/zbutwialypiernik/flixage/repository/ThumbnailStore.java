package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.Thumbnail;
import org.springframework.content.commons.repository.ContentStore;

public interface ThumbnailStore extends ContentStore<Thumbnail, byte[]> {

}
