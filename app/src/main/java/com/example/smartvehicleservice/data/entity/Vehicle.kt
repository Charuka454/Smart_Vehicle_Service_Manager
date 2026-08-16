package com.example.smartvehicleservice.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var customerId: String = "",
    var plateNumber: String = "",
    var model: String = "",
    var photoPath: String? = null,      // local file path from CameraX capture
    var latitude: Double? = null,       // garage / service location
    var longitude: Double? = null,
    var createdAt: Long = System.currentTimeMillis(),
    var isSynced: Boolean = false
)
