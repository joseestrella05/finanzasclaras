package com.finanzasclaras.app

import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import java.io.File
import java.util.Properties

object DesktopFirebase {
    fun init() {
        try {
            val userHome = System.getProperty("user.home") ?: "."
            val cacheDir = File(userHome, ".finanzasclaras").apply { mkdirs() }
            val propsFile = File(cacheDir, "firebase_store.properties")
            val props = Properties()
            if (propsFile.exists()) {
                try {
                    propsFile.inputStream().use { props.load(it) }
                } catch (_: Exception) {}
            }

            FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
                override fun store(key: String, value: String) {
                    props.setProperty(key, value)
                    try {
                        propsFile.outputStream().use { props.store(it, null) }
                    } catch (_: Exception) {}
                }

                override fun retrieve(key: String): String? = props.getProperty(key)

                override fun clear(key: String) {
                    props.remove(key)
                    try {
                        propsFile.outputStream().use { props.store(it, null) }
                    } catch (_: Exception) {}
                }

                override fun log(msg: String) {
                    println("[Firebase Desktop] $msg")
                }

                override fun getDatabasePath(name: String): File {
                    return File(cacheDir, name)
                }
            })

            val options = FirebaseOptions(
                applicationId = "1:127748342049:android:86308b4d90e43818e2fe60",
                apiKey = "AIzaSyCz-zY8DdpmEqIQhNLrjvd0LPwZpTvNTTc",
                projectId = "appfinanza-14cbe",
                storageBucket = "appfinanza-14cbe.firebasestorage.app"
            )

            Firebase.initialize(context = android.app.Application(), options = options)
            println("[Firebase Desktop] Initialized successfully")
        } catch (e: Exception) {
            println("[Firebase Desktop Init] Warning: ${e.message}")
            e.printStackTrace()
        }
    }
}
