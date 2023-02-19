package com.nitishsharma.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nitishsharma.chatapp.application.FirstChat
import io.socket.client.Socket
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    var socketIOInstance: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        socketIOInstance = (application as FirstChat).socketIO

        Timber.tag("URL").i(BuildConfig.BASE_URL)
        //asking permission for media (not useful currently)
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//            10
//        )

    }
}