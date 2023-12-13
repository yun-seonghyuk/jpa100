

INSERT INTO USERS ( EMAIL, PASSWORD, PHONE, REG_DATE, UPDATE_DATE, USER_NAME, STATUS, LOCK_YN, PASSWORD_RESET_YN)
VALUES ( 'dbstjdgur78@naver.com', '$2a$10$aVjtWrojTOJJStC5EFH6Z.nG/U4c0PXJke3pmYWElmS/M77dG3Ud2', '010-1111-2222', '2021-02-01 00:49:43.000000', null, '윤성혁', 1, 0, 0)
     , ( 'test1@gmail.com', '$2a$10$8bcV6o.TrTPAPzO7N7Q7YOaC3wFql99nw0Doy/F.AtNvc5ehTRCqu', '010-3333-4444', '2021-02-19 00:50:11.000000', null, '정혜경', 1, 0, 0)
     , ( 'test2@gmail.com', '3333', '010-5555-6666', '2021-02-19 23:27:07.000000', null, '박하은', 1, 0, 0)
     , ( 'test3@gmail.com', '4444', '010-7777-9999', '2021-02-02 00:27:51.000000', null, '박하영', 1, 0, 0);


INSERT INTO NOTICE ( CONTENTS, DELETED_DATE, DELETED, HITS, LIKES, REG_DATE, TITLE, UPDATE_DATE, USER_ID)
VALUES ( '내용2', null, false, 0, 0, '2021-02-01 01:12:37.000000', '제목2', null, 1)
     , ( '내용1', null, false, 0, 0, '2021-02-01 01:12:20.000000', '제목1', null, 1)
     , ( '내용3', null, false, 0, 0, '2021-02-01 01:13:07.000000', '제목3', null, 2)
     , ( '내용4', null, false, 0, 0, '2021-02-01 01:13:10.000000', '제목4', null, 2)
     , ( '내용5', null, false, 0, 0, '2021-02-01 01:13:12.000000', '제목5', null, 2)
     , ( '내용6', null, false, 0, 0, '2021-02-01 23:31:23.000000', '제목6', null, 1)
     , ( '내용7', null, false, 0, 0, '2021-02-01 23:31:26.000000', '제목7', null, 3)
     , ( '내용8', null, false, 0, 0, '2021-02-01 23:31:32.000000', '제목8', null, 3)
     , ( '내용9', null, false, 0, 0, '2021-02-01 23:31:35.000000', '제목9', null, 1)
     , ( '내용10', null, false, 0, 0, '2021-02-01 23:31:38.000000', '제목10', null, 1);


INSERT INTO NOTICE_LIKE ( NOTICE_ID, USER_ID)
VALUES ( 3, 1)
     , ( 4, 1)
     , ( 1, 1)
     , ( 3, 2)
     , ( 1, 4)
     , ( 2, 4);

--
INSERT INTO BOARD_TYPE ( BOARD_NAME, REG_DATE, USING_YN)
VALUES ( '게시판1', '2021-02-01 01:12:37.000000',1)
     , ( '게시판2', '2021-02-01 01:12:37.000000',1)
     , ( '문의게시판', '2021-02-01 01:12:37.000000',1);


INSERT INTO BOARD ( BOARD_TYPE_ID, USER_ID, TITLE, CONTENTS, REG_DATE, TOP_YN)
VALUES (1, 1, '게시글1', '게시글내용1', '2021-02-01 01:12:37.000000',0)
    , (1, 1, '게시글2', '게시글내용2', '2021-02-01 01:12:37.000000',0)
    , (3, 1, '문의제목', '문의내용1', '2021-02-01 01:12:37.000000',0);

INSERT INTO BOARD_COMMENT( COMMENTS, REG_DATE, BOARD_ID, USER_ID)
VALUES
('게시글1번에 대한 댓글1', '2021-02-01 01:12:37.000000', 1, 1)
     , ( '게시글1번에 대한 댓글2', '2021-02-01 01:12:37.000000', 1, 1)
     , ( '게시글2번에 대한 댓글1', '2021-02-01 01:12:37.000000', 2, 1)
     , ( '게시글1번에 대한 댓글3', '2021-02-01 01:12:37.000000', 1, 2)
;



INSERT INTO MAIL_TEMPLATE( TEMPLATE_ID, TITLE, CONTENTS, SEND_EMAIL, SEND_USER_NAME, REG_DATE)
VALUES ( 'USER_RESET_PASSWORD'
       , '{USER_NAME}님의 비밀번호 초기화 요청입니다.'
       , '<div><p>{USER_NAME}님 안녕하세요.</p><p>아래 링크를 클릭하여, 비밀번호를 초기화해 주세요.</p><p><a href="{SERVER_URL}/reset?key={RESET_PASSWORD_KEY}">초기화</a></p></div>'
       , 'seonghyuk518@gmail.com', '관리자', '2021-02-01 01:12:37.000000')
     , ( 'BOARD_ADD'
       , '{USER_NAME}님이 글을 게시하였습니다.'
       , '<div><p>제목: {BOARD_TITLE}</p><p>내용</p><div>{BOARD_CONTENTS}</div></div>'
       , 'seonghyuk518@gmail.com', '관리자', '2021-02-01 01:12:37.000000')
     , ( 'BOARD_REPLY'
       , '{USER_NAME}님이 글에 답변을 하였습니다.'
       , '<div><p>제목: {BOARD_TITLE}</p><p>내용</p><div>{BOARD_CONTENTS}</div><p>답변</p><div>{BOARD_REPLY_CONTENTS}</div></div>'
       , 'seonghyuk518@gmail.com', '관리자', '2021-02-01 01:12:37.000000')
     , ( 'USER_SERVICE_NOTICE'
       , '{USER_NAME}님 안녕하세요.'
       , '<div><p>개인정보 이용내역 안내</p><p>서비스를 잘 이용하고 계십니다.</p></div>'
       , 'seonghyuk518@gmail.com', '관리자', '2021-02-01 01:12:37.000000')
;




INSERT INTO LOGS ( TEXT, REG_DATE)
VALUES
( '로그1', '2021-03-01 01:01:01.000000')
     , ( '로그2', '2021-03-01 01:02:01.000000')
     , ( '로그3', '2021-03-01 01:03:01.000000')
     , ( '로그4', '2021-03-01 01:04:01.000000')
     , ( '로그5', '2021-03-01 01:05:01.000000')
;





