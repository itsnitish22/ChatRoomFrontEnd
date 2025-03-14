import com.project.starter.easylauncher.filter.ColorRibbonFilter
import java.io.FileInputStream
import java.util.Properties

val appConfig = rootProject.extra["appConfig"] as Map<String, Any>
val compose_version: String by rootProject.extra
val retrofit_version: String by rootProject.extra
val room_version: String by rootProject.extra
val nav_version: String by rootProject.extra
val koin_version: String by rootProject.extra
val mvvm_version: String by rootProject.extra
val splash_version: String by rootProject.extra

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
    id("com.starter.easylauncher")
    id("org.jetbrains.kotlin.plugin.compose")
}

val debugKeystorePropertiesFile = rootProject.file("env/debug_keystore.properties")
val debugKeystoreProperties = Properties().apply {
    load(FileInputStream(debugKeystorePropertiesFile))
}

val releaseKeystorePropertiesFile = rootProject.file("env/debug_keystore.properties")
val releaseKeystoreProperties = Properties().apply {
    if (releaseKeystorePropertiesFile.exists()) {
        load(FileInputStream(releaseKeystorePropertiesFile))
    }
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("$rootDir\\env\\debug_keystore.jks")
            storePassword = debugKeystoreProperties["storePassword"] as String
            keyAlias = debugKeystoreProperties["keyAlias"] as String
            keyPassword = debugKeystoreProperties["keyPassword"] as String
        }
        create("release") {
            storeFile = file("$rootDir\\env\\release_keystore.jks")
            storePassword = releaseKeystoreProperties["storePassword"] as String
            keyAlias = releaseKeystoreProperties["keyAlias"] as String
            keyPassword = releaseKeystoreProperties["keyPassword"] as String
        }
    }

    namespace = "com.nitishsharma.chatapp"
    compileSdk = 35

    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "2.0.0"
//    }

    defaultConfig {
        applicationId = appConfig["applicationId"] as String
        minSdk = 23
        targetSdk = 34
        versionCode = appConfig["versionCode"] as Int
        versionName = appConfig["versionName"] as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://chatroommainbe.azurewebsites.net/\""
            )
        }
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            buildConfigField("String", "BASE_URL", "\"http://192.168.18.103:3000/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    lintOptions {
        disable.add("NullSafeMutableLiveData")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.9.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.test:core-ktx:1.5.0")
    implementation("androidx.core:core-ktx:+")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Compose
    implementation("androidx.compose.material3:material3:1.0.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.0.1")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.runtime:runtime-livedata:$compose_version")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_version")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_version")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.24.13-rc")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("io.coil-kt:coil-compose:2.1.0")
    implementation("io.coil-kt:coil:0.9.2")

    // Socket.IO
    implementation("io.socket:socket.io-client:2.0.0") {
        exclude(group = "org.json", module = "json")
    }

    // MVVM
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$mvvm_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$mvvm_version")

    // RecyclerView and CardView
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation("androidx.fragment:fragment-ktx:1.5.7")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:30.2.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.5.0")
    implementation("com.google.firebase:firebase-database:20.2.2")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-bom:32.2.3")
    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.2")

    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit_version")
    implementation("com.google.code.gson:gson:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.github.bumptech.glide:glide:4.12.0")

    // Logging on networking calls
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // Swipe refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Room Database
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // Circular Image View
    implementation("de.hdodenhof:circleimageview:1.2.1")

    // Socket.IO (again, if needed)
    implementation("io.socket:socket.io-client:2.0.0") {
        exclude(group = "org.json", module = "json")
    }

    // Shimmer from Facebook
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Timber Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Lottie Animation from Airbnb
    implementation("com.airbnb.android:lottie:6.0.0")

    // Koin
    implementation("io.insert-koin:koin-android:$koin_version")

    // Splash Screen API
    implementation("androidx.core:core-splashscreen:$splash_version")

    // Floating Action Button
    implementation("com.github.clans:fab:1.6.4")

    api(project(":domain"))
    api(project(":data"))
}


easylauncher {
    buildTypes {
        register("debug") {
            filters(
                customRibbon(
                    gravity = ColorRibbonFilter.Gravity.TOPLEFT,
                    label = "${appConfig["versionName"]}",
                    textSizeRatio = 0.2f
                )
            )
        }
    }
}