package com.zbutwialypiernik.flixage.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;

/**
 * Superclass of every entity in this project. Id is represented as uuid.
 * Class is named as BaseEntity to avoid name conflicts with JPA
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private String id;

    @UpdateTimestamp
    @Column
    private Instant lastUpdateTime;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant creationTime;

}
