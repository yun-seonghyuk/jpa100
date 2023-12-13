package com.example.jpa.notice.controller;

import com.example.jpa.notice.entity.Notice;
import com.example.jpa.notice.exception.AlreadyDeletedException;
import com.example.jpa.notice.exception.DuplicateNoticeException;
import com.example.jpa.notice.exception.NoticeNotFoundException;
import com.example.jpa.notice.model.NoticeDeleteInput;
import com.example.jpa.notice.model.NoticeInput;
import com.example.jpa.notice.model.NoticeModel;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.notice.repository.NoticeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiNoticeController {

    private final NoticeRepository noticeRepository;

    @GetMapping("/notice0")
    public String notice0() {
        return "공지 사항 입니다.";
    }

    @GetMapping("/notice")
    public NoticeModel notice() {

        LocalDateTime regDate = LocalDateTime.now();

        NoticeModel notice = new NoticeModel();
        notice.setId(1);
        notice.setTitle("공지사항 입니다.");
        notice.setContents("공지사항 내용입니다.");
        notice.setRegDate(regDate);

        return notice;
    }

    @GetMapping("/notice2")
    public List<NoticeModel> notice2() {
        List<NoticeModel> list = new ArrayList<>();
        LocalDateTime regDate = LocalDateTime.now();

        NoticeModel notice = new NoticeModel();

        notice.setId(1);
        notice.setTitle("공지사항 입니다.");
        notice.setContents("공지사항 내용입니다.");
        notice.setRegDate(regDate);

        list.add(notice);

        NoticeModel notice2 = NoticeModel.builder()
                .id(2)
                .title("두번째 공지사항입니다.")
                .contents("두번째 공지사항 내용입니다.")
                .regDate(regDate.plusDays(1))
                .build();

        list.add(notice2);

        return list;
    }

    @GetMapping("/notice3")
    public List<NoticeModel> notice3() {
        return new ArrayList<>();
    }

    @GetMapping("notice/count")
    public int noticeCount() {

        List<NoticeModel> list = new ArrayList<>();

        LocalDateTime regDate = LocalDateTime.now();

        NoticeModel notice = new NoticeModel();

        notice.setId(1);
        notice.setTitle("공지사항 입니다.");
        notice.setContents("공지사항 내용입니다.");
        notice.setRegDate(regDate);

        list.add(notice);

        NoticeModel notice2 = NoticeModel.builder()
                .id(2)
                .title("두번째 공지사항입니다.")
                .contents("두번째 공지사항 내용입니다.")
                .regDate(regDate.plusDays(1))
                .build();

        list.add(notice2);

        return list.size();
    }

    @PostMapping("/notice")
    public NoticeModel postNotice(@RequestParam String title, @RequestParam String contents) {

        return NoticeModel.builder()
                .id(1)
                .title("첫번째 공지사항입니다.")
                .contents("첫번째 공지사항 내용입니다.")
                .regDate(LocalDateTime.now())
                .build();
    }

    @PostMapping("/notice2")
    public NoticeModel postNotice2(NoticeModel noticeModel) {

        noticeModel.setId(2);
        noticeModel.setRegDate(LocalDateTime.now());

        return noticeModel;
    }

    @PostMapping("/notice3")
    public NoticeModel postNotice3(@RequestBody NoticeModel noticeModel) {

        noticeModel.setId(3);
        noticeModel.setRegDate(LocalDateTime.now());

        return noticeModel;
    }

    @PostMapping("/notice4")
    public Notice addNotice(@RequestBody NoticeInput noticeInput) {

        Notice notice = Notice.builder()
                .title(noticeInput.getTitle())
                .contents(noticeInput.getContents())
                .regDate(LocalDateTime.now())
                .build();

        noticeRepository.save(notice);

        return notice;
    }

    @PostMapping("/notice5")
    public Notice addNotice2(@RequestBody NoticeInput noticeInput) {

        Notice notice = Notice.builder()
                .title(noticeInput.getTitle())
                .contents(noticeInput.getContents())
                .regDate(LocalDateTime.now())
                .hits(0)
                .likes(0)
                .build();

        return noticeRepository.save(notice);
    }

    @GetMapping("/notice6/{id}")
    public Notice getNotice(@PathVariable Long id) {
        Optional<Notice> notice = noticeRepository.findById(id);

        if (notice.isPresent()) {
            return notice.get();
        }
        return null;
    }

    @PutMapping("/notice/{id}")
    public ResponseEntity<Notice> updateNotice(@PathVariable Long id, @RequestBody NoticeInput noticeInput) {
        Optional<Notice> notice = noticeRepository.findById(id);

        if (notice.isPresent()) {
            notice.get().setContents(noticeInput.getTitle());
            notice.get().setContents(noticeInput.getContents());
            notice.get().setUpdateDate(LocalDateTime.now());

            noticeRepository.save(notice.get());

        }
        return ResponseEntity.ok(notice.get());

    }

    @ExceptionHandler(NoticeNotFoundException.class)
    public ResponseEntity<String> handlerNoticeNotFoundException(NoticeNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/notice1/{id}")
    public void updateNotice2(@PathVariable Long id, @RequestBody NoticeInput noticeInput) {

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항의 글이 존재하지 않습니다."));

        notice.setContents(noticeInput.getTitle());
        notice.setContents(noticeInput.getContents());
        notice.setUpdateDate(LocalDateTime.now());

        noticeRepository.save(notice);
    }

    // 부분 수정
    @PatchMapping("/notice2/{id}/hits")
    public void noticeHits(@PathVariable Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항의 글이 존재하지 않습니다."));

        notice.setHits(notice.getHits() + 1);

        noticeRepository.save(notice);
    }

    @DeleteMapping("/notice/{id}")
    public void deleteNotice(@PathVariable Long id) {

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항의 글이 존재하지 않습니다."));


        noticeRepository.delete(notice);
    }

    @ExceptionHandler(AlreadyDeletedException.class)
    public ResponseEntity<String> handlerAlreadyDeletedException(AlreadyDeletedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.OK);
    }

    @DeleteMapping("/notice2/{id}")
    public void deleteNotice2(@PathVariable Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항의 글이 존재하지 않습니다."));

        if (notice.isDeleted()) {
            throw new AlreadyDeletedException("이미 삭제된 글입니다.");
        }
        notice.setDeleted(true);
        notice.setDeletedDate(LocalDateTime.now());
        noticeRepository.save(notice);
    }

    @DeleteMapping("/notice3")
    public void deleteNoticeList(@RequestBody NoticeDeleteInput noticeDeleteInput) {

        List<Notice> noticeList = noticeRepository.findByIdIn(noticeDeleteInput.getIdList())
                .orElseThrow(() -> new NoticeNotFoundException("공지사항의 글이 존재하지 않습니다."));

        noticeList.forEach(e -> {
            e.setDeleted(true);
            e.setDeletedDate(LocalDateTime.now());
        });

        noticeRepository.saveAll(noticeList);
    }

    @DeleteMapping("/notice/all")
    public void deleteAll() {
        noticeRepository.deleteAll();
    }

//    @PostMapping("/notice")
//    public void addNotice3(@RequestBody NoticeInput noticeInput){
//
//        Notice notice = Notice.builder()
//                .title(noticeInput.getTitle())
//                .contents(noticeInput.getContents())
//                .hits(0)
//                .likes(0)
//                .regDate(LocalDateTime.now())
//                .build();
//
//        noticeRepository.save(notice);
//    }

    @PostMapping("/add-notice3")
    public ResponseEntity<Object> addNotice4(@RequestBody @Valid NoticeInput noticeInput,
                                             Errors errors) {

        if (errors.hasErrors()) {
            List<ResponseError> responseErrors = new ArrayList<>();

            errors.getAllErrors().forEach(e -> {
                responseErrors.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        noticeRepository.save(Notice.builder()
                .title(noticeInput.getTitle())
                .contents(noticeInput.getContents())
                .hits(0)
                .likes(0)
                .regDate(LocalDateTime.now())
                .build());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notice/latest/{size}")
    public Page<Notice> noticeLatest(@PathVariable int size) {
        Page<Notice> noticeList =
                noticeRepository.findAll(
                        PageRequest.of(0, size, Sort.Direction.DESC, "regDate"));

        return noticeList;
    }

    @ExceptionHandler(DuplicateNoticeException.class)
    public ResponseEntity<?> handlerDuplicateNoticeException(DuplicateNoticeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/add-notice2")
    public void addNotice23(@RequestBody NoticeInput noticeInput) {

        // 중복 체크
        LocalDateTime checkDate = LocalDateTime.now().minusMinutes(1);

        int noticeCount = noticeRepository.countByTitleAndContentsAndRegDateIsGreaterThanEqual(
                noticeInput.getTitle(),
                noticeInput.getContents(),
                checkDate);

        if (noticeCount > 0) {
            throw new DuplicateNoticeException("1분이내에 등록된 동일한 공지사항이 존재합니다.");
        }

        noticeRepository.save(Notice.builder()
                .title(noticeInput.getTitle())
                .contents(noticeInput.getContents())
                .hits(0)
                .likes(0)
                .regDate(LocalDateTime.now())
                .build());

    }
}
