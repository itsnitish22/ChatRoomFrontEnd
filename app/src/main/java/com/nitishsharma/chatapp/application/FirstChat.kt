package com.nitishsharma.chatapp.application

import android.app.Application
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.nitishsharma.chatapp.BuildConfig
import com.nitishsharma.chatapp.di.apiModule
import com.nitishsharma.chatapp.di.networkModule
import com.nitishsharma.chatapp.di.repositoryModule
import com.nitishsharma.chatapp.di.useCaseModules
import com.nitishsharma.chatapp.di.viewModelModules
import io.socket.client.IO
import io.socket.client.Socket
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import timber.log.Timber
import timber.log.Timber.Forest.plant

class FirstChat : Application() {
    var socketIO: Socket? = IO.socket(BuildConfig.BASE_URL)

    override fun onCreate() {
        super.onCreate()
        socketIO?.connect()
        plant(Timber.DebugTree())
        FirebaseApp.initializeApp(this)
        getFirebaseMessagingToken()

        startKoin()
    }

    private fun startKoin() {
        org.koin.core.context.startKoin {
            androidLogger()
            androidContext(this@FirstChat)
            modules(repositoryModule, apiModule, networkModule, useCaseModules, viewModelModules)
        }
    }

    private fun getFirebaseMessagingToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.tag("FirstChat").w(task.exception, "Fetching FCM registration token failed")
                return@OnCompleteListener
            }
            Timber.tag("FCM Token").d(task.result)
        })
    }
}