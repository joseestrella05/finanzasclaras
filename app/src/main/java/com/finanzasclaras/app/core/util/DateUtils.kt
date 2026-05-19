package com.finanzasclaras.app.core.util

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

object DateUtils {
    private val dbFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val displayFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val displayFormatShort = DateTimeFormatter.ofPattern("dd/MM")
    private val monthYearFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

    fun now(): LocalDateTime = LocalDateTime.now()

    fun today(): LocalDate = LocalDate.now()

    fun formatDb(date: LocalDateTime): String = date.format(dbFormat)

    fun parseDb(date: String): LocalDateTime = LocalDateTime.parse(date, dbFormat)

    fun formatDisplay(date: LocalDateTime): String = date.format(displayFormat)

    fun formatDisplayShort(date: LocalDateTime): String = date.format(displayFormatShort)

    fun formatMonthYear(date: LocalDate): String = date.format(monthYearFormat)

    fun startOfDay(date: LocalDate): LocalDateTime = date.atStartOfDay()

    fun endOfDay(date: LocalDate): LocalDateTime = date.atTime(23, 59, 59)

    fun startOfWeek(): LocalDate = today().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    fun endOfWeek(): LocalDate = today().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    fun startOfMonth(): LocalDate = today().withDayOfMonth(1)

    fun endOfMonth(): LocalDate {
        val ym = YearMonth.now()
        return today().withDayOfMonth(ym.lengthOfMonth())
    }

    fun startOfYear(): LocalDate = today().withDayOfYear(1)

    fun endOfYear(): LocalDate = today().withDayOfYear(today().lengthOfYear())

    fun previousMonth(): YearMonth = YearMonth.now().minusMonths(1)

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    fun toEpochMillis(date: LocalDate): Long =
        date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    fun toEpochMillisEnd(date: LocalDate): Long =
        date.atTime(23, 59, 59, 999999999).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
}
