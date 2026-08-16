package com.example.smartvehicleservice.data.repository

import com.example.smartvehicleservice.data.dao.ServiceDao
import com.example.smartvehicleservice.data.entity.ServiceRecord
import kotlinx.coroutines.flow.Flow

class ServiceRepository(private val dao: ServiceDao) {
    fun getByVehicle(vehicleId: String): Flow<List<ServiceRecord>> = dao.getByVehicle(vehicleId)
    suspend fun save(record: ServiceRecord) = dao.insert(record)
    suspend fun delete(record: ServiceRecord) = dao.delete(record)
    suspend fun getUnsynced() = dao.getUnsynced()
    suspend fun markSynced(record: ServiceRecord) = dao.update(record.copy(isSynced = true))
    suspend fun getDueReminders(now: Long) = dao.getDueReminders(now)
    suspend fun countAll() = dao.countAll()
}
