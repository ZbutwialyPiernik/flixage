package com.zbutwialypiernik.flixage.entity.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "image_file")
public class ImageFileEntity extends FileEntity {

    @Column(nullable = false)
    private long width;

    @Column(nullable = false)
    private long height;

}
