package com.finanzasclaras.app.core.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.finanzasclaras.app.core.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            4, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            Constants.SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    fun scheduleDailyReminder() {
        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            24, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            Constants.REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }

    fun cancelSync() {
        WorkManager.getInstance(context).cancelUniqueWork(Constants.SYNC_WORK_NAME)
    }

    fun cancelReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(Constants.REMINDER_WORK_NAME)
    }
}
