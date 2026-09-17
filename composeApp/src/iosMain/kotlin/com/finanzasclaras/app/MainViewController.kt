package com.finanzasclaras.app

import androidx.compose.ui.window.ComposeUIViewController
import com.finanzasclaras.app.core.di.initKoin
import org.koin.compose.KoinContext
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController {
        KoinContext {
            App()
        }
    }
}
