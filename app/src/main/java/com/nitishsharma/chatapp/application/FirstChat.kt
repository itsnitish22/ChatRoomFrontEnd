package com.nitishsharma.chatapp.application

import android.app.Application
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.nitishsharma.chatapp.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
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