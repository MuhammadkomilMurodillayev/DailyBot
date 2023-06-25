package com.example.dailyrutine.repository;

import com.example.dailyrutine.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Muhammadkomil Murodillayev, сб 23:46. 03/06/23
 */

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    Optional<History> findByTaskId(Long taskId);


    List<History> findAllByTaskId(Long taskId);

}
