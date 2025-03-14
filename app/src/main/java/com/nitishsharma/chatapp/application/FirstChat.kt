package com.nitishsharma.chatapp.application

import android.app.Application
import android.os.Handler
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
import io.socket.engineio.client.transports.WebSocket
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

class FirstChat : Application() {
    var socketIO: Socket? = null
    private val handler = Handler()
    private var isRetrying = false

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initFirebase()
        connectSocket()
        startKoin()
    }

    private fun initTimber() {
        Timber.plant(DebugTree())
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        getFirebaseMessagingToken()
    }

    private fun connectSocket() {
        try {
            val socketConfig = IO.Options().apply {
                forceNew = true
                reconnection = true
            }
            socketIO = IO.socket(BuildConfig.BASE_URL, socketConfig)
            socketIO?.connect()
            socketIO?.on(Socket.EVENT_CONNECT) {
                Timber.tag("Socket Log").i("Socket Connected")
                isRetrying = false // Reset the retry flag
            }
            socketIO?.on(Socket.EVENT_DISCONNECT) {
                Timber.tag("Socket Log").e("Socket Disconnected")
                if (!isRetrying) {
                    retrySocketConnection()
                }
            }
            socketIO?.on(Socket.EVENT_CONNECT_ERROR) {
                Timber.tag("Socket Log").e("Socket Connection Error: ${it.joinToString()}")
            }
        } catch (e: Exception) {
            Timber.tag("Socket Log").e("Can't connect to the servers")
        }
    }

    private fun retrySocketConnection() {
        isRetrying = true
        handler.postDelayed({
            try {
                Timber.tag("Socket Log").i("Retrying socket connection...")
                socketIO?.connect()
            } catch (e: SocketIOException) {
                Timber.tag("Socket Log").e("Failed to reconnect Socket.IO: ${e.message}")
                retrySocketConnection()
            }
        }, 5000)
    }

    private fun startKoin() {
        startKoin {
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
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.tag("FirstChat").w(task.exception, "Fetching FCM registration token failed")
                return@addOnCompleteListener
            }
            Timber.tag("FCM Token").d(task.result)
        }
    }
}
