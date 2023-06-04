package com.example.dailyrutine.repository;

import com.example.dailyrutine.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Muhammadkomil Murodillayev, вс 23:09. 04/06/23
 */

@Repository
public interface UserRepository extends JpaRepository<TelegramUser, Long> {

    Optional<TelegramUser> findByKey(String key);
}
