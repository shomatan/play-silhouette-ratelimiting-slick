# --- !Ups

create table users (
  user_id VARCHAR NOT NULL,
  first_name VARCHAR,
  last_name VARCHAR,
  full_name VARCHAR,
  email VARCHAR,
  rate_limit BIGINT NOT NULL,
  primary key (user_id)
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