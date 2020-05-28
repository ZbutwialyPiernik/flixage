package com.zbutwialypiernik.flixage.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.Instant;
import java.time.Instant;

/**
 * Superclass of every entity in this project. Id is represented as uuid.
 * Class is named as BaseEntity to avoid name conflicts with JPA
 */
@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name =  "UUID", strategy =  "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private String id;

    @Column
    private Instant lastUpdateTime;

    @Column(updatable = false)
    private Instant creationTime;

}
