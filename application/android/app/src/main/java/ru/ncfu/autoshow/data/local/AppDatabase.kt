package ru.ncfu.autoshow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** Локальная база данных Room (кэш каталога). */
@Database(entities = [CachedVehicleEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
}
