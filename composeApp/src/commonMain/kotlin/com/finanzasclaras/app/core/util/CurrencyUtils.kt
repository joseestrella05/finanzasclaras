package com.finanzasclaras.app.core.util

import com.finanzasclaras.app.core.common.Constants
import kotlin.math.abs
import kotlin.math.roundToLong

object CurrencyUtils {
    private val rates = mapOf(
        "DOP" to 1.0,
        "USD" to 57.5,
        "EUR" to 62.3,
        "MXN" to 3.35,
        "COP" to 0.014,
        "ARS" to 0.065,
        "CLP" to 0.062,
        "PEN" to 15.2
    )

    fun convert(amount: Double, from: String, to: String): Double {
        if (from == to) return amount
        val inBase = amount * (rates[from] ?: 1.0)
        return inBase / (rates[to] ?: 1.0)
    }

    fun format(amount: Double, currency: String): String {
        val symbol = Constants.Currencies.SYMBOLS[currency] ?: "$"
        val rounded = (amount * 100).roundToLong()
        val absVal = abs(rounded)
        val whole = absVal / 100
        val frac = (absVal % 100).toString().padStart(2, '0')
        val sign = if (rounded < 0) "-" else ""
        
        val wholeStr = whole.toString().reversed().chunked(3).joinToString(",").reversed()
        return "$sign$symbol $wholeStr.$frac"
    }

    fun round(amount: Double): Double =
        (amount * 100).roundToLong() / 100.0
}
