package com.finanzasclaras.app.core.common

object Constants {
    const val DATABASE_NAME = "finanzas_claras_db"
    const val PREFS_NAME = "finanzas_claras_prefs"
    const val DEFAULT_CURRENCY = "DOP"
    const val SYNC_WORK_NAME = "sync_work"
    const val REMINDER_WORK_NAME = "reminder_work"
    const val CHANNEL_ID_ALERTS = "finanzas_alerts"
    const val CHANNEL_ID_REMINDERS = "finanzas_reminders"
    const val CHANNEL_ID_GOALS = "finanzas_goals"
    const val NOTIFICATION_ID_LIMIT = 1001
    const val NOTIFICATION_ID_REMINDER = 1002
    const val NOTIFICATION_ID_GOAL = 1003

    object Categories {
        val EXPENSE_CATEGORIES = listOf(
            "Alimentación" to "food",
            "Transporte" to "transport",
            "Salud" to "health",
            "Ocio" to "entertainment",
            "Vivienda" to "housing",
            "Educación" to "education",
            "Servicios" to "utilities",
            "Ropa" to "clothing",
            "Otros gastos" to "other_expense"
        )

        val INCOME_CATEGORIES = listOf(
            "Salario" to "salary",
            "Inversión" to "investment",
            "Freelance" to "freelance",
            "Regalo" to "gift",
            "Otros ingresos" to "other_income"
        )
    }

    object Currencies {
        val SUPPORTED = listOf("DOP", "USD", "EUR", "MXN", "COP", "ARS", "CLP", "PEN")
        val SYMBOLS = mapOf(
            "DOP" to "RD\$",
            "USD" to "\$",
            "EUR" to "€",
            "MXN" to "MX\$",
            "COP" to "COL\$",
            "ARS" to "AR\$",
            "CLP" to "CL\$",
            "PEN" to "S/."
        )
    }
}
