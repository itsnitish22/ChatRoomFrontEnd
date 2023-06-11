package com.nitishsharma.chatapp.utils.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.nitishsharma.domain.api.interactors.IsChatActivityOpenUseCase
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class StickyService : Service(), KoinComponent {
    private val isChatActivityOpenUseCase: IsChatActivityOpenUseCase by inject()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
    }
}