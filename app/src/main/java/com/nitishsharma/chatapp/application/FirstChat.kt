package com.nitishsharma.chatapp.application

import android.app.Application
import io.socket.client.IO
import io.socket.client.Socket

class FirstChat : Application() {
    var socketIO: Socket? = IO.socket("http://192.168.18.4:3000")

    override fun onCreate() {
        super.onCreate()
        socketIO?.connect()
    }
}