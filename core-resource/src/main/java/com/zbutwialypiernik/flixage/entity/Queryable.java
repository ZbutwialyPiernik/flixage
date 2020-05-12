package com.zbutwialypiernik.flixage.entity;

import com.zbutwialypiernik.flixage.repository.ThumbnailFileStore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/** Every entity that has name and corresponding thumbnail extends this class
 * managed by {@link ThumbnailFileStore}.
*/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public abstract class Queryable extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Thumbnail thumbnail;

}
