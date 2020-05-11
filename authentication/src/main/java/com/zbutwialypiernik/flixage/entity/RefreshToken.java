package com.zbutwialypiernik.flixage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RefreshToken extends BaseEntity {

    @ManyToOne
    private User user;

    @Column
    private long expireTime;

    @Column
    private boolean isBlacklisted = false;

    public Instant getExpireDate() {
        return getCreationTime().plus(expireTime, ChronoUnit.SECONDS);
    }

    public boolean isExpired(Clock clock) {
        return Instant.now(clock).isAfter(getExpireDate());
    }

}
