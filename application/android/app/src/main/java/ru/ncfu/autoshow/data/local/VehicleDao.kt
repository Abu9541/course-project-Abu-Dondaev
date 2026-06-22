package ru.ncfu.autoshow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/** DAO кэша автомобилей. */
@Dao
interface VehicleDao {

    @Query("SELECT * FROM cached_vehicles ORDER BY id")
    suspend fun getAll(): List<CachedVehicleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(vehicles: List<CachedVehicleEntity>)

    @Query("DELETE FROM cached_vehicles")
    suspend fun clear()

    suspend fun replaceAll(vehicles: List<CachedVehicleEntity>) {
        clear()
        upsertAll(vehicles)
    }
}
