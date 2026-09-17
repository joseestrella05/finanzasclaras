package com.finanzasclaras.app

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.finanzasclaras.app.core.di.initKoin
import org.koin.compose.KoinContext

fun main() {
    DesktopFirebase.init()
    initKoin()
    application {
        val windowState = rememberWindowState(width = 1100.dp, height = 750.dp)
        Window(
            onCloseRequest = ::exitApplication,
            title = "Finanzas Claras",
            state = windowState,
            icon = painterResource("icon.png")
        ) {
            KoinContext {
                App()
            }
        }
    }
}
