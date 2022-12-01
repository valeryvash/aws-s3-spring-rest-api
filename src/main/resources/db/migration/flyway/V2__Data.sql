# Create roles
insert into roles (role_name)
values ('ROLE_USER'),
       ('ROLE_MODERATOR'),
       ('ROLE_ADMINISTRATOR');
# Populate users table
insert into users (user_name, password, email, first_name, last_name)
values ('qwerty', '$2a$12$GO2h0oSsyiDA.BDVDiPqNuY80q8YeLUlwe7ERYRGInHDcKovx4mvS', 'one@mail.com', 'Owen', 'Tangney'),
       ('qwerty2', '$2a$12$GO2h0oSsyiDA.BDVDiPqNuY80q8YeLUlwe7ERYRGInHDcKovx4mvS', 'two@mail.com', 'Lois', 'Laine'),
       ('qwerty3', '$2a$12$GO2h0oSsyiDA.BDVDiPqNuY80q8YeLUlwe7ERYRGInHDcKovx4mvS', 'three@mail.com', 'Kristen',
        'Stewart'),
       ('qwerty4', '$2a$12$GO2h0oSsyiDA.BDVDiPqNuY80q8YeLUlwe7ERYRGInHDcKovx4mvS', 'fourth@mail.com', 'Amayak',
        'Akopyan'),
       ('qwerty5', '$2a$12$GO2h0oSsyiDA.BDVDiPqNuY80q8YeLUlwe7ERYRGInHDcKovx4mvS', 'fifth@mail.com', 'Sherlock',
        'Homes'),
       ('qwerty6', '$2a$12$GO2h0oSsyiDA.BDVDiPqNuY80q8YeLUlwe7ERYRGInHDcKovx4mvS', 'six@mail.com', 'Valery', 'VaSh');

# Create views for role id
create view ROLE_USER_ID as
(
select id
from roles
order by id
limit 1 offset 0
);
create view ROLE_MODERATOR_ID as
(
select id
from roles
order by id
limit 1 offset 1
);
create view ROLE_ADMINISTRATOR_ID as
(
select id
from roles
order by id
limit 1 offset 2
);

# Assign roles to user
insert into user_roles
values ((select id from users order by id limit 1 offset 0),
        (select * from ROLE_USER_ID)),
       ((select id from users order by id limit 1 offset 1),
        (select * from ROLE_USER_ID)),
       ((select id from users order by id limit 1 offset 2),
        (select * from ROLE_USER_ID)),
       ((select id from users order by id limit 1 offset 3),
        (select * from ROLE_USER_ID)),
       ((select id from users order by id limit 1 offset 4),
        (select * from ROLE_USER_ID)),
       ((select id from users order by id limit 1 offset 4),
        (select * from ROLE_MODERATOR_ID)),
       ((select id from users order by id limit 1 offset 5),
        (select * from ROLE_USER_ID)),
       ((select id from users order by id limit 1 offset 5),
        (select * from ROLE_MODERATOR_ID)),
       ((select id from users order by id limit 1 offset 5),
        (select * from ROLE_ADMINISTRATOR_ID));

# Drop views after usage
drop view if exists ROLE_USER_ID;
drop view if exists ROLE_MODERATOR_ID;
drop view if exists ROLE_ADMINISTRATOR_ID;