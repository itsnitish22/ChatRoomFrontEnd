package com.nitishsharma.chatapp.application

import android.app.Application
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.nitishsharma.chatapp.BuildConfig
import com.nitishsharma.chatapp.di.apiModule
import com.nitishsharma.chatapp.di.networkModule
import com.nitishsharma.chatapp.di.repositoryModule
import com.nitishsharma.chatapp.di.sharedPrefModule
import com.nitishsharma.chatapp.di.useCaseModules
import com.nitishsharma.chatapp.di.viewModelModules
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import timber.log.Timber
import timber.log.Timber.Forest.plant

class FirstChat : Application() {
    var socketIO: Socket? = IO.socket(BuildConfig.BASE_URL)

    override fun onCreate() {
        super.onCreate()
        try {
            socketIO?.connect()
        } catch (e: Exception) {
            Timber.e("Can't connect to the servers")
        }
        plant(Timber.DebugTree())
        FirebaseApp.initializeApp(this)
        getFirebaseMessagingToken()
        startKoin()
    }

    private fun connectSocket() {
        try {
            val socketConfig = IO.Options().apply {
                forceNew = true
                reconnection = true
            }
            IO.socket(BuildConfig.BASE_URL, socketConfig)
            socketIO?.connect()
            socketIO?.on(Socket.EVENT_CONNECT) {
                Timber.tag("Socket Log").i("Socket Connected")
            }
            socketIO?.on(Socket.EVENT_DISCONNECT) {
                reconnectSocket()
            }

        } catch (e: Exception) {
            Timber.tag("Socket Log").e("Can't connect to the servers")
        }
    }

    private fun reconnectSocket() {
        try {
            socketIO?.connect()
        } catch (e: SocketIOException) {
            Timber.tag("Socket Log").e("Failed to reconnect Socket.IO: ${e.message}")
        }
    }

    private fun startKoin() {
        org.koin.core.context.startKoin {
            androidLogger()
            androidContext(this@FirstChat)
            modules(
                repositoryModule,
                apiModule,
                networkModule,
                useCaseModules,
                viewModelModules,
                sharedPrefModule
            )
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