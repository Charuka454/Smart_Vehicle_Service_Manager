package com.example.smartvehicleservice.sync

import com.example.smartvehicleservice.data.entity.Customer
import com.example.smartvehicleservice.data.entity.ServiceRecord
import com.example.smartvehicleservice.data.entity.Vehicle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Handles pushing local (Room) records up to Firebase Firestore.
 * Only called when the device has an internet connection.
 */
class FirebaseSyncManager {
    private val db = FirebaseFirestore.getInstance()

    suspend fun pushCustomer(customer: Customer) {
        db.collection("customers").document(customer.id).set(customer).await()
    }

    suspend fun pushVehicle(vehicle: Vehicle) {
        db.collection("vehicles").document(vehicle.id).set(vehicle).await()
    }

    suspend fun pushServiceRecord(record: ServiceRecord) {
        db.collection("service_records").document(record.id).set(record).await()
    }
}
