package com.example.dailyrutine.repository;

import com.example.dailyrutine.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Muhammadkomil Murodillayev, сб 18:04. 03/06/23
 */

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query(
            value = "select t.* from task t where t.user_id = ?1 and (t.daily_type = 'EVERY_DAY' or (t.daily_type = 'EVERY_WEEK' and to_char(t.created_at, 'day') = to_char(now(), 'day')) or (t.daily_type = 'EVERY_MONTH' and extract('month' from t.updated_at) = extract('month' from now()) and extract('day' from t.updated_at) = extract('day' from now()))  or (t.daily_type = 'EVERY_YEAR' and extract('year' from t.updated_at) = extract('year' from now()) and extract('month' from t.updated_at) = extract('month' from now()) and extract('day' from t.updated_at) = extract('day' from now())))",
            nativeQuery = true
    )
    List<Task> findAllByUserId(Long userId);

    @Query(
            value = "update task chat_id = ?1",
            nativeQuery = true
    )
    void saveChat(String chatId);

}
