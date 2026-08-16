package com.example.smartvehicleservice.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartvehicleservice.data.dao.CustomerDao
import com.example.smartvehicleservice.data.dao.ServiceDao
import com.example.smartvehicleservice.data.dao.VehicleDao
import com.example.smartvehicleservice.data.entity.Customer
import com.example.smartvehicleservice.data.entity.ServiceRecord
import com.example.smartvehicleservice.data.entity.Vehicle

@Database(
    entities = [Customer::class, Vehicle::class, ServiceRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun serviceDao(): ServiceDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_vehicle_service.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
