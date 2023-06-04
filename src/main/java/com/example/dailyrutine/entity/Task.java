package com.example.dailyrutine.entity;

import com.example.dailyrutine.enums.DailyType;
import com.example.dailyrutine.enums.VolumeType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Muhammadkomil Murodillayev, сб 12:55. 03/06/23
 */

@Entity
@Getter
@Setter
@ToString
@SequenceGenerator(name = "task_generator", sequenceName = "task_seq")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_generator")
    private Long id;

    private String name;

    @Column(columnDefinition = "boolean default false")
    private Boolean done;

    @Column(name = "created_at", columnDefinition = "timestamp default now()")
    private LocalDateTime createdAt;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "volume_type", columnDefinition = "varchar default 'MIN'")
    private VolumeType volumeType;

    @Column(name = "plan_count")
    private Integer planCount;

    @Column(name = "plan_min")
    private Long planMin;

    @Column(name = "spent_min")
    private Long spentMin;

    @Column(name = "spent_count")
    private Integer spentCount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "daily_type", columnDefinition = "varchar(20) default 'ONLY_ONE'")
    private DailyType dailyType;

    private Integer custom;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "updated_at",columnDefinition = "timestamp default now()")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Task task = (Task) o;
        return id != null && Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}






