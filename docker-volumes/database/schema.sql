create table album (id varchar(255) not null, creation_time datetime not null, last_update_time datetime, name varchar(255) not null, thumbnail_id varchar(255), artist_id varchar(255), primary key (id));
create table artist (id varchar(255) not null, creation_time datetime not null, last_update_time datetime, name varchar(255) not null, thumbnail_id varchar(255), primary key (id));
create table audio_file (id varchar(255) not null, creation_time datetime not null, last_update_time datetime, extension varchar(255), file_id varchar(255), mime_type varchar(255), size bigint not null, duration bigint not null, primary key (id));
create table image_file (id varchar(255) not null, creation_time datetime not null, last_update_time datetime, extension varchar(255), file_id varchar(255), mime_type varchar(255), size bigint not null, height bigint not null, width bigint not null, primary key (id));
create table playlist (id varchar(255) not null, creation_time datetime not null, last_update_time datetime, name varchar(255) not null, thumbnail_id varchar(255), owner_id varchar(255), primary key (id));
create table playlist_tracks (playlist_id varchar(255) not null, tracks_id varchar(255) not null);
create table track (id varchar(255) not null, creation_time datetime not null, last_update_time datetime, name varchar(255) not null, genre varchar(255), stream_count bigint not null, thumbnail_id varchar(255), album_id varchar(255), artist_id varchar(255), audio_file_id varchar(255), album varchar(255), primary key (id));
create table user (id varchar(255) not null, creation_time datetime not null, last_update_time datetime, name varchar(255) not null, last_audio_stream datetime, enabled bit not null, expired_credentials bit  not null, locked bit not null, password varchar(60) not null, role varchar(255), username varchar(32) not null, thumbnail_id varchar(255), primary key (id));
create table user_saved_tracks (user_id varchar(255) not null, saved_tracks_id varchar(255) not null);
create table refresh_token (id varchar(255) not null, creation_time datetime(6), last_update_time datetime(6), expire_time bigint, is_blacklisted bit, user_id varchar(255), primary key (id));
alter table user add constraint UK_sb8bbouer5wak8vyiiy4pf2bx unique (username);
alter table album add constraint FK6v7kfmm72rhnr7b3liclhve2m foreign key (thumbnail_id) references image_file (id);
alter table album add constraint FKmwc4fyyxb6tfi0qba26gcf8s1 foreign key (artist_id) references artist (id);
alter table artist add constraint FKs1ofurr76jy089nnu405mqf29 foreign key (thumbnail_id) references image_file (id);
alter table playlist add constraint FK71cyct8hycd1lom1xfowqc1p3 foreign key (thumbnail_id) references image_file (id);
alter table playlist add constraint FKth9shh6b71k2k9f8sdtuedunf foreign key (owner_id) references user (id);
alter table playlist_tracks add constraint FKs1ti0pm4ttt0lkx5xi86iwxcb foreign key (tracks_id) references track (id);
alter table playlist_tracks add constraint FKdmojw87kwq7bpf4h9fas2tkyj foreign key (playlist_id) references playlist (id);
alter table track add constraint FK9pe1j3hx824le458x3ugy2wag foreign key (thumbnail_id) references image_file (id);
alter table track add constraint FKaxm9pbgk7ptorfyk3o6911q05 foreign key (album_id) references album (id);
alter table track add constraint FKi28jadqiuqk1dlxtl0me7hqh2 foreign key (artist_id) references artist (id);
alter table track add constraint FKspe3cvw3t6sbyw4ah2mw0ojy2 foreign key (audio_file_id) references audio_file (id);
alter table track add constraint FKrdqpaxtar239vj2gngp8e5ujx foreign key (album) references album (id);
alter table user add constraint FKl1wprtaec08rg3lgbw6kj187a foreign key (thumbnail_id) references image_file (id);
alter table user_saved_tracks add constraint FK6kql7by8aa6uae4jj8dv65p3b foreign key (saved_tracks_id) references track (id);
alter table user_saved_tracks add constraint FKgvl661ucqwy92juwcxn97mbeh foreign key (user_id) references user (id);
alter table refresh_token add constraint FKfgk1klcib7i15utalmcqo7krt foreign key (user_id) references user (id);


