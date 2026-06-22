package ru.ncfu.autoshow.core

import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Форматирование цен, чисел и дат для отображения. */
object Format {
    private val RU = Locale("ru")
    private val OUT_DATETIME = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
    private val OUT_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val ISO_SECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    fun price(value: Double?): String {
        if (value == null) return "—"
        val nf = NumberFormat.getNumberInstance(RU).apply { maximumFractionDigits = 0 }
        return nf.format(value) + " ₽"
    }

    fun number(value: Double?): String {
        if (value == null) return "—"
        val nf = NumberFormat.getNumberInstance(RU).apply { maximumFractionDigits = 0 }
        return nf.format(value)
    }

    fun dateTime(iso: String?): String = try {
        LocalDateTime.parse(iso).format(OUT_DATETIME)
    } catch (e: Exception) {
        iso ?: "—"
    }

    fun date(iso: String?): String = try {
        LocalDateTime.parse(iso).format(OUT_DATE)
    } catch (e: Exception) {
        iso ?: "—"
    }

    /** ISO-формат для отправки даты-времени на сервер. */
    fun toIso(dt: LocalDateTime): String = dt.format(ISO_SECONDS)
}
