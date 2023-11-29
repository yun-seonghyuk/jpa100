package com.example.jpa.notice.controller;

import com.example.jpa.notice.entity.Notice;
import com.example.jpa.notice.exception.NoticeNotFoundException;
import com.example.jpa.notice.model.NoticeInput;
import com.example.jpa.notice.model.NoticeModel;
import com.example.jpa.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public void deleteNotice(@PathVariable Long id){

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항의 글이 존재하지 않습니다."));


        noticeRepository.delete(notice);
    }

    @DeleteMapping("/notice2/{id}")
    public void deleteNotice2(@PathVariable Long id){
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항의 글이 존재하지 않습니다."));

        if (notice.isDeleted()){
            
        }
    }
}
