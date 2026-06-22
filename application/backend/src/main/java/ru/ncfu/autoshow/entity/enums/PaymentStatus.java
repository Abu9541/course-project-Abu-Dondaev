package ru.ncfu.autoshow.entity.enums;

/** Статус платежа (имитация платёжного шлюза). */
public enum PaymentStatus {
    /** Платёж создан, ожидает подтверждения. */
    PENDING,
    /** Платёж успешно проведён. */
    SUCCEEDED,
    /** Платёж отклонён. */
    FAILED
}
