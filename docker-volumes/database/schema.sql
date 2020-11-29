create table album (
   id varchar(255) not null,
    creation_time datetime not null,
    update_time datetime,
    name varchar(255) not null,
    thumbnail_id varchar(255),
    artist_id varchar(255) not null,
    primary key (id)
);

create table artist (
   id varchar(255) not null,
    creation_time datetime not null,
    update_time datetime,
    name varchar(255) not null,
    thumbnail_id varchar(255),
    primary key (id)
);

create table audio_file (
   id varchar(255) not null,
    creation_time datetime not null,
    update_time datetime,
    extension varchar(255) not null,
    file_id varchar(255) not null,
    mime_type varchar(255) not null,
    size bigint not null,
    duration bigint not null,
    primary key (id)
);

create table image_file (
   id varchar(255) not null,
    creation_time datetime not null,
    update_time datetime,
    extension varchar(255) not null,
    file_id varchar(255) not null,
    mime_type varchar(255) not null,
    size bigint not null,
    height bigint not null,
    width bigint not null,
    primary key (id)
);

create table playlist (
   id varchar(255) not null,
    creation_time datetime not null,
    update_time datetime,
    name varchar(255) not null,
    thumbnail_id varchar(255),
    owner_id varchar(255) not null,
    primary key (id)
);

create table playlist_followers (
   playlist_id varchar(255) not null,
    followers_id varchar(255) not null,
    primary key (playlist_id, followers_id)
);

create table playlist_tracks (
   playlist_id varchar(255) not null,
    tracks_id varchar(255) not null
);

create table track (
   id varchar(255) not null,
    creation_time datetime not null,
    update_time datetime,
    name varchar(255) not null,
    genre varchar(255),
    thumbnail_id varchar(255),
    album_id varchar(255),
    artist_id varchar(255) not null,
    audio_file_id varchar(255),
    primary key (id)
);

create table track_stream (
   creation_time datetime not null,
    stream_count bigint not null,
    update_time datetime not null,
    user_id varchar(255) not null,
    track_id varchar(255) not null,
    primary key (track_id, user_id)
);

create table user (
   id varchar(255) not null,
    creation_time datetime not null,
    update_time datetime,
    name varchar(255) not null,
    enabled bit not null,
    expired_credentials bit not null,
    last_audio_stream datetime,
    locked bit not null,
    password varchar(60) not null,
    role varchar(255),
    username varchar(32) not null,
    thumbnail_id varchar(255),
    primary key (id)
);

create table refresh_token (
    id varchar(255) not null,
    creation_time datetime(6),
    update_time datetime(6),
    expire_time bigint,
    is_blacklisted bit,
    user_id varchar(255),
    primary key (id)
);

alter table playlist add constraint UNIQUE_PLAYLIST_SHARE_CODE unique (share_code);
alter table user add constraint UNIQUE_USER_USERNAME unique (username);
alter table album add constraint FK_ALBUM_THUMBNAIL_ID foreign key (thumbnail_id) references image_file (id);
alter table album add constraint FK_ALBUM_ARTIST_ID foreign key (artist_id) references artist (id);
alter table artist add constraint FK_ARTIST_THUMBNAIL_ID foreign key (thumbnail_id) references image_file (id);
alter table playlist add constraint FK_PLAYLIST_THUMBNAIL_ID foreign key (thumbnail_id) references image_file (id);
alter table playlist add constraint FK_PLAYLIST_OWNER_ID foreign key (owner_id) references user (id);
alter table playlist_tracks add constraint FL_PLAYLIST_TRACKS_TRACK_ID foreign key (tracks_id) references track (id);
alter table playlist_tracks add constraint FL_PLAYLIST_TRACKS_PLAYLIST_ID foreign key (playlist_id) references playlist (id);
alter table track add constraint FK_TRACK_THUMBNAIL_ID foreign key (thumbnail_id) references image_file (id);
alter table track add constraint FK_TRACK_ALBUM_ID foreign key (album_id) references album (id);
alter table track add constraint FK_TRACK_ARTIST_ID foreign key (artist_id) references artist (id);
alter table track add constraint FK_TRACK_AUDIO_FILE_ID foreign key (audio_file_id) references audio_file (id);
alter table user add constraint FK_USER_THUMBNAIL_ID foreign key (thumbnail_id) references image_file (id);
alter table playlist_followers add constraint FK_PLAYLIST_PLAYLIST_FOLLOWERS_PLAYLIST_ID foreign key (playlist_id) references playlist (id);
alter table playlist_followers add constraint FK_PLAYLIST_PLAYLIST_FOLLOWERS_USER_ID foreign key (followers_id) references user (id);
alter table refresh_token add constraint FK_REFRESH_TOKEN_USER_ID foreign key (user_id) references user (id);


