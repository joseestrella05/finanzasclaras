package com.finanzasclaras.app.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.core.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val userPreferences: UserPreferences
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = userPreferences.getPreferences()
        if (!prefs.dailyReminderEnabled) return Result.success()
        notificationHelper.showReminder()
        return Result.success()
    }
}
