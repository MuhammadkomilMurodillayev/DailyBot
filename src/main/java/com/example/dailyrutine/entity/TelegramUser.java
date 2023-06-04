package com.example.dailyrutine.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author Muhammadkomil Murodillayev, вс 22:17. 04/06/23
 */
@Entity
@Getter
@Setter
@ToString
@SequenceGenerator(name = "user_generator", sequenceName = "user_seq")
public class TelegramUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    private Long id;

    private String key;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "today_sent", columnDefinition = "boolean default false")
    private Boolean todaySent;

}
