package com.zbutwialypiernik.flixage.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class TrackStream {

    @EmbeddedId
    private TrackStreamId id;

    @Column(nullable = false)
    private long streamCount;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updateTime;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant creationTime;

}
