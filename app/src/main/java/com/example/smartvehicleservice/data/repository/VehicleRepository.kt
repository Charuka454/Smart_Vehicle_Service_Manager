package com.example.smartvehicleservice.data.repository

import com.example.smartvehicleservice.data.dao.VehicleDao
import com.example.smartvehicleservice.data.entity.Vehicle
import kotlinx.coroutines.flow.Flow

class VehicleRepository(private val dao: VehicleDao) {
    fun getAll(): Flow<List<Vehicle>> = dao.getAll()
    fun search(query: String): Flow<List<Vehicle>> = dao.search(query)
    fun getByCustomer(customerId: String): Flow<List<Vehicle>> = dao.getByCustomer(customerId)
    suspend fun getById(id: String) = dao.getById(id)
    suspend fun save(vehicle: Vehicle) = dao.insert(vehicle)
    suspend fun delete(vehicle: Vehicle) = dao.delete(vehicle)
    suspend fun getUnsynced() = dao.getUnsynced()
    suspend fun markSynced(vehicle: Vehicle) = dao.update(vehicle.copy(isSynced = true))
}
