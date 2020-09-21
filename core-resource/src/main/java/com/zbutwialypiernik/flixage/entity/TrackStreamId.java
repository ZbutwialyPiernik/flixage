package com.zbutwialypiernik.flixage.entity;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TrackStreamId implements Serializable {

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Track track;

}
