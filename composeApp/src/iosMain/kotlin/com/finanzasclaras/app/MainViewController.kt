package com.finanzasclaras.app

import androidx.compose.ui.window.ComposeUIViewController
import com.finanzasclaras.app.core.di.initKoin
import org.koin.compose.KoinContext
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    try {
        initKoin()
    } catch (_: Exception) {
        // Koin ya iniciado
    }
    return ComposeUIViewController {
        KoinContext {
            App()
        }
    }
}
