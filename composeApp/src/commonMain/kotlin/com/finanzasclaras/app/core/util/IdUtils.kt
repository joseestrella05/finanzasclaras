package com.finanzasclaras.app.core.util

import kotlinx.datetime.Clock
import kotlin.random.Random

object IdUtils {
    fun randomId(): String {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val random = Random.nextLong(100000, 999999)
        val randHex = Random.nextInt().toUInt().toString(16)
        return "${timestamp}-${random}-${randHex}"
    }
}
