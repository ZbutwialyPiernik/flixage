package com.zbutwialypiernik.flixage.entity.file;

import com.zbutwialypiernik.flixage.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class FileEntity extends BaseEntity {

    @Column(nullable = false)
    private String extension;

    @MimeType
    @Column(nullable = false)
    private String mimeType;

    @ContentId
    @Column(nullable = false)
    private String fileId;

    @ContentLength
    @Column(nullable = false)
    private long size;

}
