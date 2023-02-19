package com.nitishsharma.chatapp.application

import android.app.Application
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
    }
}