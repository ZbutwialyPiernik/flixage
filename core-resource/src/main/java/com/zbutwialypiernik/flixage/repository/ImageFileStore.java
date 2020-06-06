package com.zbutwialypiernik.flixage.repository;

import com.zbutwialypiernik.flixage.entity.file.ImageFileEntity;
import org.springframework.content.commons.repository.ContentStore;

public interface ImageFileStore extends ContentStore<ImageFileEntity, String> {

}
