package com.nitishsharma.chatapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.nitishsharma.chatapp.base.BaseActivity
import com.nitishsharma.chatapp.databinding.ActivityMainBinding
import com.nitishsharma.chatapp.notification.FCMService
import org.koin.core.component.KoinComponent
import timber.log.Timber


class MainActivity : BaseActivity<ActivityMainBinding>(), KoinComponent {
    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("URL").i(BuildConfig.BASE_URL)
        askReadWritePermission()
        if (firebaseAuth?.currentUser != null)
            FCMService.subscribeToFirebaseTopic(firebaseAuth?.currentUser?.uid)
    }

    private fun askReadWritePermission() {
        //asking permission for media (not useful currently)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            10
        )
    }
}