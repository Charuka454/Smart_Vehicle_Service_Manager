package com.example.smartvehicleservice.data.dao

import androidx.room.*
import com.example.smartvehicleservice.data.entity.ServiceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ServiceRecord)

    @Update
    suspend fun update(record: ServiceRecord)

    @Delete
    suspend fun delete(record: ServiceRecord)

    @Query("SELECT * FROM service_records WHERE vehicleId = :vehicleId ORDER BY serviceDate DESC")
    fun getByVehicle(vehicleId: String): Flow<List<ServiceRecord>>

    @Query("SELECT * FROM service_records WHERE isSynced = 0")
    suspend fun getUnsynced(): List<ServiceRecord>

    @Query("SELECT * FROM service_records WHERE nextReminderDate IS NOT NULL AND nextReminderDate <= :now")
    suspend fun getDueReminders(now: Long): List<ServiceRecord>

    @Query("SELECT COUNT(*) FROM service_records")
    suspend fun countAll(): Int
}
