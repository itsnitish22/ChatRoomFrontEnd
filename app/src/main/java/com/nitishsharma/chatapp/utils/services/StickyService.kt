package com.nitishsharma.chatapp.utils.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class StickyService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
    }
}