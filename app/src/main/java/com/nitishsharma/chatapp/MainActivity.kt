package com.nitishsharma.chatapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.nitishsharma.chatapp.base.BaseActivity
import com.nitishsharma.chatapp.databinding.ActivityMainBinding
import com.nitishsharma.chatapp.utils.services.StickyService
import timber.log.Timber


class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("URL").i(BuildConfig.BASE_URL)
        askReadWritePermission()
        startStickyService()
    }

    private fun startStickyService() {
        val stickyService = Intent(this, StickyService::class.java)
        startService(stickyService)
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