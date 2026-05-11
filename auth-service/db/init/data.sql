create table users
(
    id         bigint auto_increment    primary key,
    name       varchar(255)             not null,
    email      varchar(255)             not null,
    password   varchar(255)             not null,
    role       enum ('ADMIN', 'CLIENT') not null,
    active     bit                      not null,
    created_at datetime(6)              not null,
    updated_at datetime(6)              not null
);
