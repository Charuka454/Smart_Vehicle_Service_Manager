package com.example.smartvehicleservice.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartvehicleservice.data.AppDatabase

/**
 * Runs periodically. Checks Room for any service record whose
 * nextReminderDate has passed and fires a local notification.
 */
class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val due = db.serviceDao().getDueReminders(System.currentTimeMillis())

        NotificationHelper.createChannel(applicationContext)
        due.forEachIndexed { index, record ->
            NotificationHelper.showReminder(
                applicationContext,
                id = record.id.hashCode(),
                title = "Service Reminder",
                message = "A vehicle is due for: ${record.serviceType}"
            )
        }
        return Result.success()
    }
}
