package com.example.smartvehicleservice.data.dao

import androidx.room.*
import com.example.smartvehicleservice.data.entity.Vehicle
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle)

    @Update
    suspend fun update(vehicle: Vehicle)

    @Delete
    suspend fun delete(vehicle: Vehicle)

    @Query("SELECT * FROM vehicles ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE plateNumber LIKE '%' || :query || '%' OR model LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE customerId = :customerId")
    fun getByCustomer(customerId: String): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getById(id: String): Vehicle?

    @Query("SELECT * FROM vehicles WHERE isSynced = 0")
    suspend fun getUnsynced(): List<Vehicle>
}
