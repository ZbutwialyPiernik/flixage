package com.zbutwialypiernik.flixage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Duration;
import java.time.Instant;

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

    public void setExpireTime(Duration expireTime) {
        this.expireTime = expireTime.toMillis();
    }

    public Duration getExpireTime() {
        return Duration.ofMillis(expireTime);
    }

    public Instant getExpireDate() {
        return getCreationTime().plus(getExpireTime());
    }

}
