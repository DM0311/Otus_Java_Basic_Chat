drop database if exists otus_chat;

create database otus_chat;

\c otus_chat;

drop schema if exists console_chat;

create schema console_chat;

alter role "chat_server" with login;

drop role if exists chat_server;

create role chat_server with password 'admin';

grant connect on database otus_chat to chat_server;

grant all privileges on schema console_chat to chat_server;

DROP TABLE if exists console_chat.users;

CREATE TABLE console_chat.users (
	user_id int4 DEFAULT nextval('console_chat.newtable_user_id_seq'::regclass) NOT NULL,
	login varchar NOT NULL,
	user_name varchar NOT NULL,
	"password" varchar NOT NULL,
	"role" varchar NOT NULL,
	last_activity numeric NULL,
	isbanned bool NULL,
	ban_time numeric NULL,
	CONSTRAINT users_pk PRIMARY KEY (user_name)
);
--Insert ADMIN user
INSERT INTO console_chat.users (name, owner, pwd) VALUES ('Admin', 'admin', 'admin_pwd');

DROP TABLE if exists console_chat.room_history;

CREATE TABLE console_chat.room_history (
	room_name varchar NOT NULL,
	"time_stamp" numeric NOT NULL,
	message_text varchar NOT NULL,
	from_user varchar NOT NULL,
	CONSTRAINT room_history_rooms_fk FOREIGN KEY (room_name) REFERENCES console_chat.rooms("name") ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE if exists console_chat.rooms

CREATE TABLE console_chat.rooms (
	"name" varchar NOT NULL,
	"owner" varchar NOT NULL,
	pwd varchar NOT NULL,
	last_activity numeric NULL,
	CONSTRAINT rooms_pk PRIMARY KEY (name)
);
--Insert start room
INSERT INTO console_chat.rooms (name, owner, pwd) VALUES ('Default', 'Admin', ' ');