package com.example.jpa.user.repository;

import com.example.jpa.user.model.UserLogCount;
import com.example.jpa.user.model.UserNoticeCount;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCustomRepository  {

    private final EntityManager entityManager;

    public List<UserNoticeCount> findUserNoticeCount() {
        String sql = "SELECT u.id, u.email, u.user_name, " +
                "(SELECT COUNT(*) FROM Notice n WHERE n.user_id = u.id) AS notice_count FROM `Users` u";

        return entityManager.createNativeQuery(sql).getResultList();
    }

    public List<UserLogCount> findUserLogCount() {
        String sql = "SELECT u.id, u.email, u.user_name, " +
                "(SELECT COUNT(*) FROM notice n WHERE n.user_id = u.id) AS notice_count, " +
                "(SELECT COUNT(*) FROM notice_like nl WHERE nl.user_id = u.id) AS notice_like_count " +
                "FROM `Users` u";

        return entityManager.createNativeQuery(sql).getResultList();
    }

    public List<UserLogCount> findUserLikeBest() {
        String sql = " select t1.id, t1.email, t1.user_name, t1.notice_like_count " +
                " from (" +
                " select u.* ,(select count(*) from notice_like nl where nl.user_id = u.id) as notice_like_count "+
                " from users u ) t1 "+
                " order by t1.notice_like_count desc ";

        return entityManager.createNativeQuery(sql).getResultList();
    }
}

