package com.example.jpa.board.service;

import com.example.jpa.board.entity.*;
import com.example.jpa.board.model.*;
import com.example.jpa.board.repository.*;
import com.example.jpa.common.MailComponent;
import com.example.jpa.common.exception.BixException;
import com.example.jpa.mail.MailTemplate;
import com.example.jpa.mail.MailTemplateRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardTypeRepository boardTypeRepository;
    private final BoardRepository boardRepository;
    private final BoardTypeCustomRepository boardTypeCustomRepository;
    private final BoardHitsRepository boardHitsRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardBadReportRepository boardBadReportRepository;
    private final BoardScrapRepository boardScrapRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final BoardCommentRepository boardCommentRepository;

    private final UserRepository userRepository;

    private final MailTemplateRepository mailTemplateRepository;
    private final MailComponent mailComponent;

    @Override
    public ServiceResult addBoard(BoardTypeInput boardTypeInput) {

        BoardType boardType = boardTypeRepository.findByBoardName(boardTypeInput.getName());

        if (boardType != null && boardTypeInput.getName().equals(boardType.getBoardName())) {
            // 동일한 게시판제목이 있다.
            return ServiceResult.fail("이미 동일한 게시판이 존재합니다.");
        }

        BoardType addBoardType = BoardType.builder()
                .boardName(boardTypeInput.getName())
                .regDate(LocalDateTime.now())
                .build();

        boardTypeRepository.save(addBoardType);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult updateBoard(Long id, BoardTypeInput boardTypeInput) {
        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(id);

        if (optionalBoardType.isEmpty()) {
            return ServiceResult.fail("수정할 게시판 타입이 없습니다.");
        }

        BoardType boardType = optionalBoardType.get();

        if (boardType.getBoardName().equals(boardTypeInput.getName())) {
            return ServiceResult.fail("수정할 이름이 동일한 게시판명 입니다.");
        }

        boardType.setBoardName(boardTypeInput.getName());
        boardType.setUpdateDate(LocalDateTime.now());
        boardTypeRepository.save(boardType);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult deleteBoard(Long id) {

        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(id);
        if (optionalBoardType.isEmpty()) {
            return ServiceResult.fail("삭제할 게시판타입이 없습니다.");
        }

        BoardType boardType = optionalBoardType.get();
        if (boardRepository.countByBoardType(boardType) > 0) {
            return ServiceResult.fail("삭제할 게시판타입의 게시글이 존재합니다.");
        }

        boardTypeRepository.delete(boardType);
        return ServiceResult.success();
    }

    @Override
    public List<BoardType> getAllBoardType() {
        return boardTypeRepository.findAll();
    }

    @Override
    public ServiceResult setBoardTypeUsing(Long id, BoardTypeUsing boardTypeUsing) {
        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(id);
        if (optionalBoardType.isEmpty()) {
            return ServiceResult.fail("삭제할 게시판타입이 없습니다.");
        }

        BoardType boardType = optionalBoardType.get();
        boardType.setUsingYn(boardTypeUsing.isUsingYn());

        boardTypeRepository.save(boardType);

        return ServiceResult.success();
    }

    @Override
    public List<BoardTypeCount> getBoardTypeCount() {
        return boardTypeCustomRepository.getBoardTypeCount();
    }

    @Override
    public ServiceResult setBoardTop(Long id, boolean topYn) {

        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            return ServiceResult.fail("게시글이 존재하지 않습니다.");
        }

        Board board = optionalBoard.get();

        if (board.isTopYn() == topYn) {
            if (topYn) {
                return ServiceResult.fail("이미 게시글이 최상단에 배치되어 있습니다.");
            } else {
                return ServiceResult.fail("이미 게시글이 최상단 배치가 해제되어 있습니다.");
            }
        }

        board.setTopYn(topYn);
        boardRepository.save(board);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult setBoardPeriod(Long id, BoardPeriod boardPeriod) {
        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            return ServiceResult.fail("게시글이 존재하지 않습니다.");
        }

        Board board = optionalBoard.get();

        board.setPublishStartDate(boardPeriod.getStartDate());
        board.setPublishEndDate(boardPeriod.getEndDate());

        boardRepository.save(board);

        return ServiceResult.success();
    }

    @Override
    public ServiceResult setBoardHits(Long id, String email) {

        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            return ServiceResult.fail("게시글이 존재하지 않습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("회원 정보가 존재하지 않습니다.");
        }

        User user = optionalUser.get();

        if (boardHitsRepository.countByBoardAndUser(board, user) > 0) {
            return ServiceResult.fail("이미 조회수가 있습니다.");
        }

        boardHitsRepository.save(BoardHits.builder()
                .board(board)
                .user(user)
                .regDate(LocalDateTime.now())
                .build());

        return ServiceResult.success();
    }

    @Override
    public ServiceResult setBoardLike(Long id, String email) {
        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            return ServiceResult.fail("게시글이 존재하지 않습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("회원 정보가 존재하지 않습니다.");
        }

        User user = optionalUser.get();

        long boardLikeCount = boardLikeRepository.countByBoardAndUser(board, user);
        if (boardLikeCount > 0) {
            return ServiceResult.fail("이미 좋아요한 내용이 있습니다.");
        }

        boardLikeRepository.save(BoardLike.builder()
                .board(board)
                .user(user)
                .regDate(LocalDateTime.now())
                .build());

        return ServiceResult.success();
    }

    @Override
    public ServiceResult setBoardUnLike(Long id, String email) {
        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            return ServiceResult.fail("게시글이 존재하지 않습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("회원 정보가 존재하지 않습니다.");
        }

        User user = optionalUser.get();

        Optional<BoardLike> optionalBoardLike = boardLikeRepository.findByBoardAndUser(board, user);
        if (optionalBoardLike.isEmpty()) {
            return ServiceResult.fail("좋아요한 내용이 없습니다");
        }

        BoardLike boardLike = optionalBoardLike.get();

        boardLikeRepository.delete(boardLike);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult addBadReport(Long id, String email, BoardBadReportInput boardBadReportInput) {
        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            return ServiceResult.fail("게시글이 존재하지 않습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("회원 정보가 존재하지 않습니다.");
        }

        User user = optionalUser.get();

        BoardBadReport boardBadReport = BoardBadReport.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .userEmail(user.getEmail())

                .boardId(board.getId())
                .boardUserId(board.getUser().getId())
                .boardTitle(board.getTitle())
                .boardContents(board.getContents())
                .boardRegDate(board.getRegDate())

                .comment(boardBadReportInput.getComment())
                .build();

        boardBadReportRepository.save(boardBadReport);
        return ServiceResult.success();
    }

    @Override
    public List<BoardBadReport> badReportList() {
        return boardBadReportRepository.findAll();
    }

    @Override
    public ServiceResult scrapBoard(Long id, String email) {
        Optional<Board> optionalBoard = boardRepository.findById(id);

        if (optionalBoard.isEmpty()) {
            return ServiceResult.fail("게시글이 존재하지 않습니다.");
        }
        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("회원 정보가 존재하지 않습니다.");
        }
        User user = optionalUser.get();

        BoardScrap boardScrap = BoardScrap.builder()
                .user(user)
                .boardId(board.getId())
                .boardTypeId(board.getBoardType().getId())
                .boardTitle(board.getTitle())
                .boardContents(board.getContents())
                .boardRegDate(board.getRegDate())

                .regDate(LocalDateTime.now())
                .build();

        boardScrapRepository.save(boardScrap);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult removeScrap(Long id, String email) {

        Optional<BoardScrap> optionalBoardScrap = boardScrapRepository.findById(id);
        if (optionalBoardScrap.isEmpty()) {
            return ServiceResult.fail("삭제할 스크랩이 없습니다.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("회원 정보가 존재하지 않습니다.");
        }

        User user = optionalUser.get();
        BoardScrap boardScrap = optionalBoardScrap.get();
        if (!Objects.equals(user.getId(), boardScrap.getUser().getId())) {
            return ServiceResult.fail("본인의 스크랩만 삭제할 수 있습니다.");
        }

        boardScrapRepository.delete(boardScrap);
        return ServiceResult.success();
    }

    private String getBoardUrl(long boardId) {
        return String.format("/board/%d", boardId);
    }

    @Override
    public ServiceResult addBookmark(Long id, String email) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isEmpty()) {
            return ServiceResult.fail("게시글이 존재하지 않습니다.");
        }

        Board board = optionalBoard.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("회원 정보가 존재하지 않습니다.");
        }

        User user = optionalUser.get();

        BoardBookmark boardBookmark = BoardBookmark.builder()
                .user(user)
                .boardId(board.getId())
                .boardTypeId(board.getBoardType().getId())
                .boardTitle(board.getTitle())
                .boardUrl(getBoardUrl(board.getId()))
                .regDate(LocalDateTime.now())
                .build();

        boardBookmarkRepository.save(boardBookmark);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult removeBookmark(Long id, String email) {
        Optional<BoardBookmark> optionalBoardBookmark = boardBookmarkRepository.findById(id);
        if (optionalBoardBookmark.isEmpty()) {
            return ServiceResult.fail("삭제할 북마크가 없습니다.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ServiceResult.fail("회원 정보가 존재하지 않습니다.");
        }

        User user = optionalUser.get();
        BoardBookmark boardBookmark = optionalBoardBookmark.get();

        if (!Objects.equals(user.getId(), boardBookmark.getUser().getId())) {
            return ServiceResult.fail("본인의 북마크만 삭제할 수 있습니다.");
        }

        boardBookmarkRepository.delete(boardBookmark);
        return ServiceResult.success();
    }

    @Override
    public List<Board> postList(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new BixException("회원정보가 존재하지 않습니다");
        }

        User user = optionalUser.get();
        return boardRepository.findByUser(user);
    }

    @Override
    public List<BoardComment> commentList(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new BixException("회원정보가 존재하지 않습니다");
        }

        User user = optionalUser.get();

        return boardCommentRepository.findByUser(user);
    }

    @Override
    public Board detail(Long id) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isEmpty()) {
            throw new BixException("게시글이 존재하지 않습니다.");
        }

        return optionalBoard.get();
    }

    @Override
    public List<Board> list() {
        return boardRepository.findAll();
    }

    @Override
    public ServiceResult add(String email, BoardInput boardInput) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new BixException("회원정보가 존재하지 않습니다");
        }

        User user = optionalUser.get();

        Optional<BoardType> optionalBoardType = boardTypeRepository.findById(boardInput.getBoardType());
        if (optionalBoardType.isEmpty()) {
            return ServiceResult.fail("게시판 정보가 존재하지 않습니다.");
        }

        BoardType boardType = optionalBoardType.get();

        Board board = Board.builder()
                .user(user)
                .boardType(boardType)
                .title(boardInput.getTitle())
                .contents(boardInput.getContent())
                .regDate(LocalDateTime.now())
                .build();

        boardRepository.save(board);

        // 메일 전송 로직
        Optional<MailTemplate> optionalMailTemplate = mailTemplateRepository.findByTemplateId("BOARD_ADD");
        optionalMailTemplate.ifPresent((e) -> {

            String fromEmail = e.getSendEmail();
            String fromUserName = e.getSendUserName();
            String title = e.getTitle().replaceAll("\\{USER_NAME\\}", user.getUserName());
            String contents = e.getContents().replaceAll("\\{BOARD_TITLE\\}", board.getTitle())
                    .replaceAll("\\{BOARD_CONTENTS\\}", board.getContents());

            mailComponent.send(fromEmail, fromUserName, user.getEmail(), user.getUserName(), title, contents);
        });


        return ServiceResult.success();
    }

    @Override
    public ServiceResult replyBoard(Long id, BoardReplyInput boardReplyInput) {

        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isEmpty()) {
            return ServiceResult.fail("게시글이 존재하지 않습니다.");
        }

        Board board = optionalBoard.get();
        board.setReplyContents(boardReplyInput.getReplyContents());

        boardRepository.save(board);

        // 메일전송
        Optional<MailTemplate> optionalMailTemplate = mailTemplateRepository.findByTemplateId("BOARD_REPLY");
        optionalMailTemplate.ifPresent((e) -> {

            String fromEmail = e.getSendEmail();
            String fromUserName = e.getSendUserName();
            String title = e.getTitle().replaceAll("\\{USER_NAME\\}", board.getUser().getUserName());
            String contents = e.getContents().replaceAll("\\{BOARD_TITLE\\}", board.getTitle())
                    .replaceAll("\\{BOARD_CONTENTS\\}", board.getContents())
                    .replaceAll("\\{BOARD_REPLY_CONTENTS\\}", board.getReplyContents());

            mailComponent.send(fromEmail, fromUserName
                    , board.getUser().getEmail(), board.getUser().getUserName(), title, contents);
        });

        return ServiceResult.success();
    }

}
