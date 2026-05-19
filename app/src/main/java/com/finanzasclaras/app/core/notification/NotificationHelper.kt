package com.finanzasclaras.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.finanzasclaras.app.MainActivity
import com.finanzasclaras.app.core.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    Constants.CHANNEL_ID_ALERTS,
                    "Alertas financieras",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alertas de límites de gasto y balance negativo"
                },
                NotificationChannel(
                    Constants.CHANNEL_ID_REMINDERS,
                    "Recordatorios",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Recordatorios para registrar transacciones"
                },
                NotificationChannel(
                    Constants.CHANNEL_ID_GOALS,
                    "Metas de ahorro",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificaciones de metas de ahorro alcanzadas"
                }
            )
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }

    fun showBudgetAlert(categoryName: String, limit: Double, spent: Double) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Límite de gasto alcanzado")
            .setContentText("Has gastado $spent de $limit en $categoryName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_LIMIT, notification)
    }

    fun showExpenseExceedsIncomeAlert() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Gastos superan ingresos")
            .setContentText("Tus gastos de este mes superan tus ingresos. Revisa tus finanzas.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_LIMIT + 1, notification)
    }

    fun showGoalReached(goalName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_GOALS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Meta alcanzada!")
            .setContentText("Felicitaciones! Alcanzaste tu meta: $goalName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_GOAL, notification)
    }

    fun showReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Recordatorio financiero")
            .setContentText("No olvides registrar tus transacciones de hoy")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_REMINDER, notification)
    }
}
