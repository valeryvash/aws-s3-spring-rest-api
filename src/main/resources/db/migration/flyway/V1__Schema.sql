create table if not exists users
(
    id         bigint auto_increment primary key,
    created    timestamp    not null default now(),
    updated    timestamp    not null default now(),
    status     varchar(25)  not null default 'ACTIVE',
    user_name  varchar(100) not null unique,
    password   varchar(255) not null,
    first_name varchar(100) not null,
    last_name  varchar(100) not null,
    email      varchar(100) not null unique
);

create table if not exists roles
(
    id        bigint auto_increment primary key,
    created   timestamp    not null default now(),
    updated   timestamp    not null default now(),
    status    varchar(25)  not null default 'ACTIVE',
    role_name varchar(100) not null default 'ROLE_USER' unique
);

create table if not exists user_roles
(
    user_id bigint not null,
    role_id bigint not null
);

alter table user_roles
    add constraint FK_user_user_roles_id
        foreign key (user_id)
            references users (id)
            on delete cascade
            on update restrict,
    add constraint FK_role_user_roles_id
        foreign key (role_id)
            references roles (id)
            on delete cascade
            on update restrict;


create table files
(
    id        bigint auto_increment primary key,
    file_name varchar(255) not null,
    file_path varchar(255) not null
);

create table events
(
    id         bigint auto_increment primary key,
    created    timestamp   not null default now(),
    event_type varchar(50) not null default 'CREATED',
    file_id    bigint      not null,
    user_id    bigint      not null
);

alter table events
    add constraint FK_file_id
        foreign key (file_id)
            references files (id)
            on delete cascade
            on update restrict,
    add constraint FK_user_id
        foreign key (user_id)
            references users (id)
            on delete cascade
            on update restrict;
