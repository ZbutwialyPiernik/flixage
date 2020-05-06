package com.zbutwialypiernik.flixage.entity;

import com.zbutwialypiernik.flixage.entity.BaseEntity;
import com.zbutwialypiernik.flixage.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RefreshToken extends BaseEntity {

    @ManyToOne(targetEntity = User.class)
    private User user;

    @Column
    private long expireTime;

    @Column
    private boolean isBlacklisted = false;

    public LocalDateTime getExpireDate() {
        return getCreationTime().plus(expireTime, ChronoUnit.SECONDS);
    }

    public boolean isExpired(Clock clock) {
        return LocalDateTime.now(clock).isAfter(getExpireDate());
    }

}
