package com.nitishsharma.chatapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nitishsharma.chatapp.databinding.ActivityChatBinding
import okhttp3.*


class ChatActivity : AppCompatActivity(), TextWatcher {
    private lateinit var binding: ActivityChatBinding
    private lateinit var name: String
    private lateinit var webSocket: WebSocket
    private var SERVER_PATH = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)

        name = intent.getStringExtra("name").toString()
//        initiateSocketConnection()
        initializeViews()
        setContentView(binding.root)
    }

    private fun initiateSocketConnection() {
        val client = OkHttpClient()
        val request = Request.Builder().url(SERVER_PATH).build()
        webSocket = client.newWebSocket(request, SocketListener())
    }

    inner class SocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            this@ChatActivity.runOnUiThread {
                Toast.makeText(this@ChatActivity, "Connection made", Toast.LENGTH_SHORT).show()
            }
            initializeViews()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
        }
    }

    private fun initializeViews() {
        binding.messageEdit.addTextChangedListener(this@ChatActivity)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val string = s.toString().trim()
        if (string.isEmpty())
            resetEditMessage()
        else {
            binding.pickImgBtn.visibility = View.INVISIBLE
            binding.sendBtn.visibility = View.VISIBLE
        }
    }

    private fun resetEditMessage() {
        binding.messageEdit.removeTextChangedListener(this@ChatActivity)
        binding.messageEdit.setText("")
        binding.sendBtn.visibility = View.INVISIBLE
        binding.pickImgBtn.visibility = View.VISIBLE

        binding.messageEdit.addTextChangedListener(this@ChatActivity)
    }
}