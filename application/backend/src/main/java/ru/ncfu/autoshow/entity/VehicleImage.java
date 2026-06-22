package ru.ncfu.autoshow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Изображение из фотогалереи автомобиля. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vehicle_images")
public class VehicleImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false, length = 512)
    private String url;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public VehicleImage(String url, Integer sortOrder) {
        this.url = url;
        this.sortOrder = sortOrder;
    }
}
