package com.nitishsharma.chatapp.notification

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class FCMService : FirebaseMessagingService() {
    private var notificationBroadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        super.onCreate()
        notificationBroadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onNewToken(token: String) {
        Timber.tag("FCM Token: ").i(token)
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
            broadcastFCMNotification(it)
            TODO("Foreground Notification")
        }
    }

    private fun broadcastFCMNotification(it: RemoteMessage.Notification) {
        CoroutineScope(Dispatchers.IO).launch {
            Timber.tag("FCM Message: ").i(it.body)
            val intent = Intent("FCMData")
            intent.putExtra("source", "FCM")
            intent.putExtra("title", it.title)
            intent.putExtra("message", it.body)
            notificationBroadcaster?.sendBroadcast(intent)
        }
    }
}