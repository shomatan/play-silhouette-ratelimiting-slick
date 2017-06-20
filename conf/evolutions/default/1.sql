# --- !Ups

create table users (
  id bigserial,
  firstName VARCHAR,
  lastName VARCHAR,
  fullName VARCHAR,
  email VARCHAR,
  avatarURL VARCHAR,
  gitbucket_token text,
  created_at bigint not null,
  updated_at bigint not null,
  primary key (id)
);

create table login_info (
  id bigserial,
  provider_id VARCHAR NOT NULL,
  provider_key VARCHAR NOT NULL,
  primary key (id)
);

create table user_login_info (
  user_id VARCHAR NOT NULL,
  login_info_id BIGINT NOT NULL
);

create table password_info (
  hasher VARCHAR NOT NULL,
  password VARCHAR NOT NULL,
  salt VARCHAR,
  login_info_id BIGINT NOT NULL
);

# --- !Downs

drop table password_info;
drop table user_login_info;
drop table login_info;
drop table users;