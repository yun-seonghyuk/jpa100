package com.example.jpa.board.repository;

import com.example.jpa.board.model.BoardTypeCount;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BoardTypeCustomRepository {

    private final EntityManager entityManager;

    public List<BoardTypeCount> getBoardTypeCount() {
        String sql = "select bt.id, bt.board_name, bt.reg_date, bt.using_yn " +
                ", (select count(*) from board b where b.board_type_id = bt.id) as board_count " +
                "from board_type bt";

        List<Object[]> result = entityManager.createNativeQuery(sql).getResultList();

        return result.stream().map(BoardTypeCount::new)
                .collect(Collectors.toList());

////        Query nativeQuery = entityManager.createNativeQuery(sql);
////        JpaResultMapper jpaResultMapper = new JpaResultMapper();
////        List<BoardTypeCount> resultList = jpaResultMapper.list(nativeQuery, BoardTypeCount.class);
//        return resultList;
    }
}

