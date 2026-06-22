package ru.ncfu.autoshow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Автомобиль в избранном у пользователя (связь N:M). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "favorites",
        uniqueConstraints = @UniqueConstraint(name = "uq_fav_user_vehicle", columnNames = {"user_id", "vehicle_id"}))
public class Favorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    public Favorite(User user, Vehicle vehicle) {
        this.user = user;
        this.vehicle = vehicle;
    }
}
