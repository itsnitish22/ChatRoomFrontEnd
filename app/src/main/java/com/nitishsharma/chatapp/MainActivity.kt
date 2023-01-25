package com.nitishsharma.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nitishsharma.chatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.enterRoomBtn.setOnClickListener {
            if (binding.nameEditText.text.toString().isEmpty())
                Toast.makeText(this, "Enter name first", Toast.LENGTH_LONG).show()
            else
                startChatActivity(binding.nameEditText.text.toString())
        }

        setContentView(binding.root)
    }

    private fun startChatActivity(name: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("name", name)
        startActivity(intent)
    }
}