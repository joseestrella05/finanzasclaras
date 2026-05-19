package com.finanzasclaras.app.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.data.remote.firebase.FirebaseSyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: FirebaseSyncManager,
    private val userPreferences: UserPreferences
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = userPreferences.getPreferences()
        if (!prefs.isLoggedIn || !prefs.syncEnabled) return Result.success()

        return when (val result = syncManager.syncAll()) {
            is com.finanzasclaras.app.data.remote.firebase.SyncResult.Success -> {
                userPreferences.setLastSyncTimestamp(System.currentTimeMillis())
                Result.success()
            }
            is com.finanzasclaras.app.data.remote.firebase.SyncResult.NotLoggedIn -> Result.success()
            is com.finanzasclaras.app.data.remote.firebase.SyncResult.Error -> Result.retry()
        }
    }
}
