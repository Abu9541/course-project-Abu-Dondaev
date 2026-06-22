package ru.ncfu.autoshow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ncfu.autoshow.entity.enums.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Автомобиль (конкретная единица с VIN). Содержит характеристики, цену,
 * статус наличия и бизнес-методы управления жизненным циклом наличия.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false, length = 120)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, unique = true, length = 17)
    private String vin;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "body_type", nullable = false, length = 20)
    private BodyType bodyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type", nullable = false, length = 20)
    private EngineType engineType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Transmission transmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "drive_type", nullable = false, length = 10)
    private DriveType driveType;

    @Column(nullable = false, length = 40)
    private String color;

    @Column(nullable = false)
    private Integer mileage = 0;

    @Column(name = "power_hp", nullable = false)
    private Integer powerHp;

    @Column(name = "engine_volume", precision = 3, scale = 1)
    private BigDecimal engineVolume;

    @Column(name = "fuel_consumption", precision = 4, scale = 1)
    private BigDecimal fuelConsumption;

    @Column(name = "equipment_level", length = 60)
    private String equipmentLevel;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status = VehicleStatus.IN_STOCK;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VehicleImage> images = new ArrayList<>();

    // ----------------------------- бизнес-методы -----------------------------

    /** Доступен ли автомобиль для записи на тест-драйв, брони или покупки. */
    public boolean isAvailable() {
        return status == VehicleStatus.IN_STOCK;
    }

    /**
     * Резервирование автомобиля под заказ.
     * @throws IllegalStateException если автомобиль недоступен.
     */
    public void reserve() {
        if (!isAvailable()) {
            throw new IllegalStateException("Автомобиль недоступен для брони (статус: " + status + ")");
        }
        this.status = VehicleStatus.RESERVED;
    }

    /** Перевод автомобиля в статус «продан». */
    public void markSold() {
        this.status = VehicleStatus.SOLD;
    }

    /** Возврат автомобиля в доступный остаток (например, при отмене брони). */
    public void returnToStock() {
        if (status != VehicleStatus.SOLD) {
            this.status = VehicleStatus.IN_STOCK;
        }
    }

    /** Добавление изображения в галерею с сохранением двусторонней связи. */
    public void addImage(VehicleImage image) {
        image.setVehicle(this);
        this.images.add(image);
    }

    /** Полное наименование «Марка Модель, год». */
    public String fullName() {
        String brandName = (brand != null) ? brand.getName() : "";
        return (brandName + " " + model + ", " + year).trim();
    }
}
