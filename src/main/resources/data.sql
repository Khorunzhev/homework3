insert into AUTHOR (`FULLNAME`) values ('Иванов');
insert into AUTHOR (`FULLNAME`) values ('Сидоров');

insert into GENRE (`NAME`) values ('Фантастика');
insert into GENRE (`NAME`) values ('Детектив');

insert into BOOK (`TITLE`, AUTHOR_ID, GENRE_ID) values ('NAME1', 1, 1);
insert into BOOK (`TITLE`, AUTHOR_ID, GENRE_ID) values ('NAME2', 2, 2);
insert into BOOK (`TITLE`, AUTHOR_ID, GENRE_ID) values ('NAME3', 1, 1);

insert into COMMENT (`TEXT`, BOOK_ID) values ('Коммент', 1);

insert into USERS (`USERNAME`, `PASSWORD`, `ROLE`) values ('admin', '$2a$10$R7NIY9xaunw5K6IisHZxaOFqCCJAo4d1U7nKTEDEC.4z4MVZqGyO.', 'ADMIN');
insert into USERS (`USERNAME`, `PASSWORD`, `ROLE`) values ('user', '$2a$10$R7NIY9xaunw5K6IisHZxaOFqCCJAo4d1U7nKTEDEC.4z4MVZqGyO.', 'USER');
