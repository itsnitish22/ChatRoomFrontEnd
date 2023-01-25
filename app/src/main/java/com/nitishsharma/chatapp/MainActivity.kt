package com.nitishsharma.chatapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.nitishsharma.chatapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            10
        )

        binding.enterRoomBtn.setOnClickListener {
            if (binding.nameEditText.text.toString().isEmpty())
                Toast.makeText(this, "Enter name first", Toast.LENGTH_LONG).show()
            else
                startChatActivity(binding.nameEditText.text.toString())
        }

    }

    private fun startChatActivity(name: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("name", name)
        startActivity(intent)
    }
}