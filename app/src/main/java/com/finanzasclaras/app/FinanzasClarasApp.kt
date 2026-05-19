package com.finanzasclaras.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.finanzasclaras.app.core.worker.WorkScheduler
import com.finanzasclaras.app.data.local.SeedData
import com.finanzasclaras.app.data.local.dao.CategoryDao
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FinanzasClarasApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workScheduler: WorkScheduler

    @Inject
    lateinit var categoryDao: CategoryDao

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        seedCategories()
        workScheduler.scheduleSync()
        workScheduler.scheduleDailyReminder()
    }

    private fun seedCategories() {
        applicationScope.launch {
            val existing = categoryDao.getAll().first()
            if (existing.isEmpty()) {
                categoryDao.insertAll(SeedData.getDefaultCategories())
            }
        }
    }
}
