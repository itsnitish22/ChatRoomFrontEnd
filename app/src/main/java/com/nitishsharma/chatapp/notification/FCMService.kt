package com.nitishsharma.chatapp.notification

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class FCMService : FirebaseMessagingService() {
    private var notificationBroadcaster: LocalBroadcastManager? = null

    companion object {
        fun subscribeToFirebaseTopic(uid: String?) {
            val userUIDFromPref = FirebaseAuth.getInstance().uid
            Timber.tag("FCM UID").i(userUIDFromPref)
            FirebaseMessaging.getInstance().subscribeToTopic("$uid-chatmsg")
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Timber.tag("FCM Subscription: ").i("Successfully subscribed to topic")
                    } else {
                        Timber.tag("FCM Subscription: ").e("Failed to subscribe to topic")
                    }
                }.addOnFailureListener {
                    Timber.tag("FCM Subscription: ").e("Failed to subscribe to topic")
                }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationBroadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("FCM Token").i(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let { notification ->
            Timber.tag("FCM Message").i(notification.body)
            broadcastFCMNotification(notification.title, notification.body)
            // Handle foreground notification here
        }
    }

    private fun broadcastFCMNotification(title: String?, message: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            Timber.tag("FCM Notification").i("Title: $title, Message: $message")
            val intent = Intent("FCMData")
            intent.putExtra("source", "FCM")
            intent.putExtra("title", title)
            intent.putExtra("message", message)
            notificationBroadcaster?.sendBroadcast(intent)
        }
    }
}
