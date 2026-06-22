package ru.ncfu.autoshow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Марка автомобиля. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "brands")
public class Brand extends BaseEntity {

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 80)
    private String country;

    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    public Brand(String name, String country) {
        this.name = name;
        this.country = country;
    }
}
