package com.finanzasclaras.app.core.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object DateUtils {
    private val timeZone: TimeZone
        get() = TimeZone.currentSystemDefault()

    fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(timeZone).date

    fun now(): LocalDateTime =
        Clock.System.now().toLocalDateTime(timeZone)

    fun startOfDay(date: LocalDate): Long =
        date.atStartOfDayIn(timeZone).toEpochMilliseconds()

    fun endOfDay(date: LocalDate): Long =
        date.atTime(23, 59, 59, 999_000_000).toInstant(timeZone).toEpochMilliseconds()

    fun startOfWeek(): LocalDate {
        val today = today()
        var current = today
        while (current.dayOfWeek != DayOfWeek.MONDAY) {
            current = current.minus(1, DateTimeUnit.DAY)
        }
        return current
    }

    fun endOfWeek(): LocalDate {
        val today = today()
        var current = today
        while (current.dayOfWeek != DayOfWeek.SUNDAY) {
            current = current.plus(1, DateTimeUnit.DAY)
        }
        return current
    }

    fun startOfMonth(): LocalDate {
        val today = today()
        return LocalDate(today.year, today.month, 1)
    }

    fun endOfMonth(): LocalDate {
        val today = today()
        val nextMonth = today.plus(1, DateTimeUnit.MONTH)
        val firstOfNext = LocalDate(nextMonth.year, nextMonth.month, 1)
        return firstOfNext.minus(1, DateTimeUnit.DAY)
    }

    fun startOfPreviousMonth(): LocalDate {
        val today = today()
        val prev = today.minus(1, DateTimeUnit.MONTH)
        return LocalDate(prev.year, prev.month, 1)
    }

    fun endOfPreviousMonth(): LocalDate {
        val firstOfThis = startOfMonth()
        return firstOfThis.minus(1, DateTimeUnit.DAY)
    }

    fun startOfYear(): LocalDate {
        val today = today()
        return LocalDate(today.year, Month.JANUARY, 1)
    }

    fun endOfYear(): LocalDate {
        val today = today()
        return LocalDate(today.year, Month.DECEMBER, 31)
    }

    fun formatDisplay(epochMillis: Long): String {
        val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(timeZone)
        val day = dt.dayOfMonth.toString().padStart(2, '0')
        val month = dt.monthNumber.toString().padStart(2, '0')
        return "$day/$month/${dt.year}"
    }

    fun formatDisplayShort(epochMillis: Long): String {
        val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(timeZone)
        val day = dt.dayOfMonth.toString().padStart(2, '0')
        val month = dt.monthNumber.toString().padStart(2, '0')
        return "$day/$month"
    }

    fun formatMonthYear(epochMillis: Long): String {
        val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(timeZone)
        val monthName = when (dt.month) {
            Month.JANUARY -> "Enero"
            Month.FEBRUARY -> "Febrero"
            Month.MARCH -> "Marzo"
            Month.APRIL -> "Abril"
            Month.MAY -> "Mayo"
            Month.JUNE -> "Junio"
            Month.JULY -> "Julio"
            Month.AUGUST -> "Agosto"
            Month.SEPTEMBER -> "Septiembre"
            Month.OCTOBER -> "Octubre"
            Month.NOVEMBER -> "Noviembre"
            Month.DECEMBER -> "Diciembre"
        }
        return "$monthName ${dt.year}"
    }

    fun formatTimestamp(timestamp: Long): String {
        val dt = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(timeZone)
        val day = dt.dayOfMonth.toString().padStart(2, '0')
        val month = dt.monthNumber.toString().padStart(2, '0')
        val hour = dt.hour.toString().padStart(2, '0')
        val minute = dt.minute.toString().padStart(2, '0')
        return "$day/$month/${dt.year} $hour:$minute"
    }

    fun toEpochMillis(date: LocalDate): Long =
        date.atStartOfDayIn(timeZone).toEpochMilliseconds()

    fun toEpochMillisEnd(date: LocalDate): Long =
        date.atTime(23, 59, 59, 999_000_000).toInstant(timeZone).toEpochMilliseconds()
}
