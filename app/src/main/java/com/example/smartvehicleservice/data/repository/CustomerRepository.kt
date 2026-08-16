package com.example.smartvehicleservice.data.repository

import com.example.smartvehicleservice.data.dao.CustomerDao
import com.example.smartvehicleservice.data.entity.Customer
import kotlinx.coroutines.flow.Flow

class CustomerRepository(private val dao: CustomerDao) {
    fun getAll(): Flow<List<Customer>> = dao.getAll()
    fun search(query: String): Flow<List<Customer>> = dao.search(query)
    suspend fun getById(id: String) = dao.getById(id)
    suspend fun save(customer: Customer) = dao.insert(customer)
    suspend fun delete(customer: Customer) = dao.delete(customer)
    suspend fun getUnsynced() = dao.getUnsynced()
    suspend fun markSynced(customer: Customer) = dao.update(customer.copy(isSynced = true))
}
