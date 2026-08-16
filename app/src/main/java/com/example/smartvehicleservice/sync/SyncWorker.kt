package com.example.smartvehicleservice.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartvehicleservice.data.AppDatabase
import com.example.smartvehicleservice.utils.NetworkUtils

/**
 * Background WorkManager job: finds every Room record with isSynced = false
 * and uploads it to Firestore, then marks it as synced locally.
 * This is what makes the app "offline-first, auto-sync when online".
 */
class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!NetworkUtils.isOnline(applicationContext)) {
            return Result.retry()
        }

        return try {
            val db = AppDatabase.getInstance(applicationContext)
            val firebase = FirebaseSyncManager()

            val unsyncedCustomers = db.customerDao().getUnsynced()
            for (c in unsyncedCustomers) {
                firebase.pushCustomer(c)
                db.customerDao().update(c.copy(isSynced = true))
            }

            val unsyncedVehicles = db.vehicleDao().getUnsynced()
            for (v in unsyncedVehicles) {
                firebase.pushVehicle(v)
                db.vehicleDao().update(v.copy(isSynced = true))
            }

            val unsyncedRecords = db.serviceDao().getUnsynced()
            for (r in unsyncedRecords) {
                firebase.pushServiceRecord(r)
                db.serviceDao().update(r.copy(isSynced = true))
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
