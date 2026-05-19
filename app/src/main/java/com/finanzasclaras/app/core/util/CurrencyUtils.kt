package com.finanzasclaras.app.core.util

import com.finanzasclaras.app.core.common.Constants
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

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
        val fmt = NumberFormat.getNumberInstance(Locale.getDefault())
        fmt.minimumFractionDigits = 2
        fmt.maximumFractionDigits = 2
        return "$symbol ${fmt.format(amount)}"
    }

    fun round(amount: Double): Double =
        BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).toDouble()
}
