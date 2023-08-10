insert into user_tb (username,password,email) values ('ssar','$2a$10$wRcWzC7P1u6gNdjVMBgPx.6O0kxXPW16irBTVXgDhgKsem72XoqI2','ssar@nate.com');
insert into user_tb (username,password,email) values ('cos','$2a$10$wRcWzC7P1u6gNdjVMBgPx.6O0kxXPW16irBTVXgDhgKsem72XoqI2','cos@nate.com');
insert into board_tb (title,content,user_id,created_at) values('제목1','내용1',1, now());
insert into board_tb (title,content,user_id,created_at) values('제목2','내용2',1, now());
insert into board_tb (title,content,user_id,created_at) values('제목3','내용3',1, now());
insert into board_tb (title,content,user_id,created_at) values('제목4','내용4',2, now());
insert into board_tb (title,content,user_id,created_at) values('제목5','내용5',2, now());
insert into reply_tb(user_id, board_id, comment) values(1,1,'안뇽');
insert into reply_tb(user_id, board_id, comment) values(1,1,'은혜 언니 메롱');
  