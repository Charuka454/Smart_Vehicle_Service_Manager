package com.example.smartvehicleservice.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var phone: String = "",
    var address: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var isSynced: Boolean = false   // true once pushed to Firestore
)
