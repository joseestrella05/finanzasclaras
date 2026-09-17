package com.finanzasclaras.app

import android.app.Application
import com.finanzasclaras.app.core.di.initKoin
import com.finanzasclaras.app.data.local.database.appAndroidContext
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.koin.android.ext.koin.androidContext

class FinanzasClarasApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appAndroidContext = this

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:127748342049:android:86308b4d90e43818e2fe60")
                    .setApiKey("AIzaSyCz-zY8DdpmEqIQhNLrjvd0LPwZpTvNTTc")
                    .setProjectId("appfinanza-14cbe")
                    .setStorageBucket("appfinanza-14cbe.firebasestorage.app")
                    .build()
                FirebaseApp.initializeApp(this, options)
            }
            Firebase.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initKoin {
            androidContext(this@FinanzasClarasApp)
        }
    }
}
