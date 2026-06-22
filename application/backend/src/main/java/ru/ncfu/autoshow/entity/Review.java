package ru.ncfu.autoshow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Отзыв пользователя об автомобиле (оценка 1..5 и комментарий). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reviews",
        uniqueConstraints = @UniqueConstraint(name = "uq_review_user_vehicle", columnNames = {"user_id", "vehicle_id"}))
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    public Review(User user, Vehicle vehicle, Integer rating, String comment) {
        this.user = user;
        this.vehicle = vehicle;
        this.rating = rating;
        this.comment = comment;
    }
}
