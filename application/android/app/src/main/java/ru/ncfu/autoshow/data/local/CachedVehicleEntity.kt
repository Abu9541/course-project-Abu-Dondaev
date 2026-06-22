package ru.ncfu.autoshow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.ncfu.autoshow.data.remote.dto.VehicleSummaryDto

/** Локальный кэш карточек каталога для оффлайн-режима. */
@Entity(tableName = "cached_vehicles")
data class CachedVehicleEntity(
    @PrimaryKey val id: Long,
    val brandName: String?,
    val model: String,
    val year: Int,
    val price: Double,
    val bodyType: String,
    val engineType: String,
    val transmission: String,
    val driveType: String,
    val powerHp: Int,
    val color: String,
    val mileage: Int,
    val equipmentLevel: String?,
    val imageUrl: String?,
    val status: String
) {
    fun toDto() = VehicleSummaryDto(
        id, brandName, model, year, price, bodyType, engineType, transmission,
        driveType, powerHp, color, mileage, equipmentLevel, imageUrl, status
    )

    companion object {
        fun from(d: VehicleSummaryDto) = CachedVehicleEntity(
            d.id, d.brandName, d.model, d.year, d.price, d.bodyType, d.engineType,
            d.transmission, d.driveType, d.powerHp, d.color, d.mileage,
            d.equipmentLevel, d.imageUrl, d.status
        )
    }
}
