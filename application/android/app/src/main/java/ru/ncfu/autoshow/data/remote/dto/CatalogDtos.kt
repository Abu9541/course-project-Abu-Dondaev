package ru.ncfu.autoshow.data.remote.dto

data class BrandDto(
    val id: Long,
    val name: String,
    val country: String?,
    val logoUrl: String?
)

data class BrandRequestDto(
    val name: String,
    val country: String?,
    val logoUrl: String?
)

data class VehicleSummaryDto(
    val id: Long,
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
)

data class VehicleDto(
    val id: Long,
    val brand: BrandDto?,
    val model: String,
    val year: Int,
    val vin: String,
    val price: Double,
    val bodyType: String,
    val engineType: String,
    val transmission: String,
    val driveType: String,
    val color: String,
    val mileage: Int,
    val powerHp: Int,
    val engineVolume: Double?,
    val fuelConsumption: Double?,
    val equipmentLevel: String?,
    val description: String?,
    val imageUrl: String?,
    val status: String,
    val images: List<String>?,
    val averageRating: Double?,
    val reviewCount: Long,
    val createdAt: String?
)

data class VehicleRequestDto(
    val brandId: Long,
    val model: String,
    val year: Int,
    val vin: String,
    val price: Double,
    val bodyType: String,
    val engineType: String,
    val transmission: String,
    val driveType: String,
    val color: String,
    val mileage: Int,
    val powerHp: Int,
    val engineVolume: Double?,
    val fuelConsumption: Double?,
    val equipmentLevel: String?,
    val description: String?,
    val imageUrl: String?,
    val status: String?,
    val images: List<String>?
)

data class PageDto<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)
