package com.example.smartvehicleservice

import android.app.Application
import androidx.work.*
import com.example.smartvehicleservice.notification.NotificationHelper
import com.example.smartvehicleservice.notification.ReminderWorker
import com.example.smartvehicleservice.sync.SyncWorker
import java.util.concurrent.TimeUnit

class SmartVehicleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        schedulePeriodicSync()
        schedulePeriodicReminders()
    }

    private fun schedulePeriodicReminders() {
        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "service_reminders",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }

    // Runs automatically in the background and pushes any un-synced
    // local Room records to Firestore whenever internet is available.
    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "firebase_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
