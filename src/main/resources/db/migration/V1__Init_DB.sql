create sequence hibernate_sequence start with 1 increment by 1;
create table user_log (id bigint not null, log_address varchar(256), log_create timestamp, log_text varchar(1024), primary key (id));
create table chat_users (id bigint not null, enable boolean, password varchar(250), username varchar(50), last_visited timestamp, primary key (id));
create table message (id bigint not null, createtime timestamp, file_name varchar(1024), file_orig_name varchar(1024), file_size varchar(250), message_text varchar(4096), author_id bigint, primary key (id));
create table receivers_message (message_id bigint not null, user_id bigint not null, primary key (message_id, user_id));
create table user_role (user_id bigint not null, roles varchar(255));
alter table message add constraint FK3ffemo2n06rm3epu65lvl3tti foreign key (author_id) references chat_users;
alter table receivers_message add constraint FK22plt9p99ja5mg4uo2vq2qctu foreign key (user_id) references chat_users;
alter table receivers_message add constraint FK58brfmx6p5pfeu4f9ll14bdlf foreign key (message_id) references message;
alter table user_role add constraint FKocmkg01v5d2wtwkfbprno6f34 foreign key (user_id) references chat_users;
 