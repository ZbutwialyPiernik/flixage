package com.zbutwialypiernik.flixage.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Thumbnail {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name =  "UUID", strategy =  "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private String id;

    @ContentId
    @Column
    private String fileId;

    @ContentLength
    @Column
    private long size;

}
