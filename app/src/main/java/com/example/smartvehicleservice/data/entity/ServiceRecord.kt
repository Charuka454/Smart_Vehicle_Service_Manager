package com.example.smartvehicleservice.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "service_records")
data class ServiceRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var vehicleId: String = "",
    var serviceType: String = "",
    var notes: String = "",
    var serviceDate: Long = System.currentTimeMillis(),
    var nextReminderDate: Long? = null,  // used to schedule a notification
    var isSynced: Boolean = false
)
