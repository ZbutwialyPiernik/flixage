package com.zbutwialypiernik.flixage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class User extends Queryable implements UserDetails {

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Playlist> playlists = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Track> savedTracks = new ArrayList<>();

    @Column
    private boolean enabled = true;

    @Column
    private boolean expired = false;

    @Column
    private boolean locked = false;

    @Column
    private boolean expiredCredentials = false;

    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.toString()));
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

    public boolean canLogin() {
        return isAccountNonExpired() && isAccountNonLocked() && isCredentialsNonExpired();
    }

}
