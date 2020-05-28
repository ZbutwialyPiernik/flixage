package com.zbutwialypiernik.flixage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

import javax.persistence.*;
import java.time.Duration;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Track extends Queryable {

    @Column
    @Enumerated(EnumType.STRING)
    private MusicGenre genre;

    @ManyToOne
    private Album album;

    @ManyToOne
    private Artist artist;

    // In seconds
    private long duration;

    @Column
    private String extension;

    @MimeType
    @Column
    private String mimeType;

    @ContentId
    @Column
    private String fileId;

    @ContentLength
    @Column
    private long fileSize;

    public void setDuration(Duration duration) {
        this.duration = duration.toSeconds();
    }

    public Duration getDuration() {
        return Duration.ofSeconds(duration);
    }

}
