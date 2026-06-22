package ru.ncfu.autoshow.entity.enums;

/** Статус наличия автомобиля. */
public enum VehicleStatus {
    /** Доступен для записи/брони/покупки. */ IN_STOCK,
    /** Зарезервирован под заказ. */          RESERVED,
    /** Продан. */                            SOLD,
    /** Временно недоступен. */               UNAVAILABLE
}
