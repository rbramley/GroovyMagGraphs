create database six_degrees;

use six_degrees;

create table MOVIE (movie_id integer not null auto_increment primary key, movie_name varchar(255) not null);

create table ACTOR (actor_id integer not null auto_increment primary key, actor_name varchar(255) not null, unique index(actor_name));

create table MOVIE_ACTOR (
 movie_id integer not null references MOVIE (movie_id), 
 actor_id integer not null references ACTOR (actor_id),
 primary key (movie_id, actor_id));

insert into movie values (1,'Footloose'), (2,'Shrek'), (3,'Charlie''s Angels'), (4,'54'), (5,'Wild Things');

insert into actor values (1,'Kevin Bacon'), (2,'John Lithgow'), (3,'Mike Myers'), (4,'Cameron Diaz'), (5,'Bill Murray'), (6, 'Neve Campbell');

insert into movie_actor values (1,1),(1,2), (2,2),(2,3),(2,4), (3,4),(3,5), (4,3),(4,6), (5,1),(5,5),(5,6);


select k.actor_name, m.movie_name, a.actor_name, n.movie_name, b.actor_name
from 
 actor k, actor a, actor b,
 movie m, movie n,
 movie_actor mk, movie_actor ma, movie_actor na, movie_actor nb
where 
 k.actor_name = 'Kevin Bacon'
and k.actor_id = mk.actor_id
and mk.movie_id = m.movie_id
and ma.movie_id = m.movie_id
and ma.actor_id = a.actor_id
and na.actor_id = a.actor_id
and na.movie_id = n.movie_id
and nb.movie_id = n.movie_id
and nb.actor_id = b.actor_id
and k.actor_id <> a.actor_id
and k.actor_id <> b.actor_id
and a.actor_id <> b.actor_id
and m.movie_id <> n.movie_id;
