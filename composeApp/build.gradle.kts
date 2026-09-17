import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("androidx.room")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)

                // Navigation & Lifecycle ViewModel
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.8.3")

                // Koin DI
                implementation("io.insert-koin:koin-core:4.0.0")
                implementation("io.insert-koin:koin-compose:4.0.0")
                implementation("io.insert-koin:koin-compose-viewmodel:4.0.0")

                // Coroutines & Serialization & DateTime
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

                // Key-Value Storage
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.2.0")

                // Room KMP
                implementation("androidx.room:room-runtime:2.7.0-alpha10")
                implementation("androidx.sqlite:sqlite-bundled:2.5.0-alpha10")


                // GitLive Firebase KMP (Auth + Firestore)
                implementation("dev.gitlive:firebase-auth:2.1.0")
                implementation("dev.gitlive:firebase-firestore:2.1.0")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation("androidx.activity:activity-compose:1.9.2")
                implementation("androidx.appcompat:appcompat:1.7.0")
                implementation("androidx.core:core-ktx:1.13.1")
                implementation("io.insert-koin:koin-android:4.0.0")
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = "com.finanzasclaras.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.finanzasclaras.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspCommonMainMetadata", "androidx.room:room-compiler:2.7.0-alpha10")
    add("kspAndroid", "androidx.room:room-compiler:2.7.0-alpha10")
    add("kspDesktop", "androidx.room:room-compiler:2.7.0-alpha10")
    add("kspIosX64", "androidx.room:room-compiler:2.7.0-alpha10")
    add("kspIosArm64", "androidx.room:room-compiler:2.7.0-alpha10")
    add("kspIosSimulatorArm64", "androidx.room:room-compiler:2.7.0-alpha10")
}


compose.desktop {
    application {
        mainClass = "com.finanzasclaras.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "FinanzasClaras"
            packageVersion = "1.0.0"
            modules("java.management", "java.naming", "java.sql", "jdk.unsupported")
            macOS {
                bundleID = "com.finanzasclaras.app"
                iconFile.set(project.file("src/desktopMain/resources/icon.icns"))
            }
        }
    }
}
