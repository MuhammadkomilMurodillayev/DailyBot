package com.example.dailyrutine.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Muhammadkomil Murodillayev, сб 13:56. 03/06/23
 */

@Entity
@Getter
@Setter
@ToString
@SequenceGenerator(name = "history_generator", sequenceName = "history_seq")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_generator")
    private Long id;

    @Column(name = "created_at", columnDefinition = "timestamp default now()")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @Column(name = "plan_count", columnDefinition = "bigint default 0")
    private Long totalPlanCount;

    @Column(name = "plan_min", columnDefinition = "bigint default 0")
    private Long totalPlanMin;

    @Column(name = "spent_min", columnDefinition = "bigint default 0")
    private Long totalSpentMin;

    @Column(name = "spent_count", columnDefinition = "bigint default 0")
    private Long totalSpentCount;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        History history = (History) o;
        return id != null && Objects.equals(id, history.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
