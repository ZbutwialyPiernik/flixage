package com.zbutwialypiernik.flixage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends Queryable implements UserDetails {

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @JoinColumn(name="owner_id")
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Playlist> playlists = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Track> savedTracks = new ArrayList<>();

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean expired = false;

    @Column(nullable = false)
    private boolean locked = false;

    @Column(nullable = false)
    private boolean expiredCredentials = false;

    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role.toAuthority());
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !expiredCredentials;
    }

}
