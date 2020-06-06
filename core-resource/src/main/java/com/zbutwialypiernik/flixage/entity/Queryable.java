package com.zbutwialypiernik.flixage.entity;

import com.zbutwialypiernik.flixage.entity.file.ImageFileEntity;
import com.zbutwialypiernik.flixage.repository.ImageFileStore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/** Every entity that has name and corresponding thumbnail extends this class
 * managed by {@link ImageFileStore}.
*/
@Getter
@Setter
@MappedSuperclass
public abstract class Queryable extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ImageFileEntity thumbnail;

}
