drop table if exists users;
drop table if exists roles;
drop table if exists user_roles;
drop table if exists files;
drop table if exists events;

create table if not exists users
(
    id         bigint       not null auto_increment,
    created    timestamp    not null default now(),
    updated    timestamp    not null default now(),
    status     varchar(25)  not null default 'ACTIVE',

    user_name  varchar(100) not null unique,
    password   varchar(255) not null,

    first_name varchar(100) not null,
    last_name  varchar(100) not null,
    email      varchar(100) not null unique,

    primary key (id)
);

create table if not exists roles
(
    id        bigint       not null auto_increment,
    created   timestamp    not null default now(),
    updated   timestamp    not null default now(),
    status    varchar(25)  not null default 'ACTIVE',

    role_name varchar(100) not null default 'ROLE_USER' unique,

    primary key (id)
);

create table if not exists user_roles
(
    user_id bigint not null,
    role_id bigint not null,

    constraint FK_user_user_roles_id
        foreign key (user_id)
            references users (id)
            on delete cascade
            on update restrict,
    constraint FK_role_user_roles_id
        foreign key (role_id)
            references roles (id)
            on delete cascade
            on update restrict
);

create table files
(
    id        bigint       not null auto_increment,

    file_name varchar(255) not null,

    primary key (id)
);

create table events
(
    id         bigint      not null auto_increment,
    created    timestamp   not null default now(),
    event_type varchar(50) not null default 'CREATED',

    file_id    bigint      not null,
    user_id    bigint      not null,

    primary key (id),

    constraint FK_file_id
        foreign key (file_id)
            references files (id)
            on delete cascade
            on update restrict,

    constraint FK_user_id
        foreign key (user_id)
            references users (id)
            on delete cascade
            on update restrict
);
