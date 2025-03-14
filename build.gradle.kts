extra["kotlin_version"] = "2.0.0"
extra["nav_version"] = "2.7.2"
extra["room_version"] = "2.5.2"
extra["mvvm_version"] = "2.6.2"
extra["retrofit_version"] = "2.9.0"
extra["compose_version"] = "1.5.1"
extra["koin_version"] = "3.4.3"
extra["splash_version"] = "1.0.0-beta02"

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.6.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.2")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.3")
        classpath("com.google.firebase:perf-plugin:1.4.2")
        classpath("com.project.starter:easylauncher:6.1.0")
    }
}

plugins {
    id("com.android.application") version "8.6.1" apply false
    id("com.android.library") version "8.6.1" apply false
    // Updated here as well.
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.secrets_gradle_plugin") version "0.4"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}

allprojects {
    extra["appConfig"] = mapOf(
        "applicationId" to "com.nitishsharma.chatapp",
        "appName" to "Chat Room",
        "versionCode" to 1,
        "versionName" to "1.0.0"
    )
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}