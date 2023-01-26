package com.nitishsharma.chatapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitishsharma.chatapp.databinding.ActivityChatBinding
import okhttp3.*
import org.json.JSONObject


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var name: String
    private lateinit var webSocket: WebSocket
    private var SERVER_PATH =
        "wss://s8293.blr1.piesocket.com/v3/1?api_key=GoHXyxyEaNrhLpxXbs2oFtog93M7OiS7Q2TTgPWf&notify_self=1"
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        name = intent.getStringExtra("name").toString()
        initializeViews()
        initiateSocketConnection()
        binding.sendBtn.setOnClickListener {
            sendTextMessage()
        }
    }

    private fun sendTextMessage() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", name)
            jsonObject.put("message", binding.messageEdit.text.toString())
            webSocket.send(jsonObject.toString())
            jsonObject.put("isSent", true)
            messageAdapter.addItem(jsonObject)
            binding.recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            resetEditMessage()
        } catch (e: Exception) {
            Log.e("Msg Delivery Fail ", e.toString())
        }
    }

    private fun initiateSocketConnection() {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(SERVER_PATH).build()
        webSocket = client.newWebSocket(request, SocketListener())
    }

    inner class SocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            this@ChatActivity.runOnUiThread {
                Toast.makeText(this@ChatActivity, "Connection made", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            this@ChatActivity.runOnUiThread {
                try {
                    val jsonObject = JSONObject(text)
                    jsonObject.put("isSent", false)
                    messageAdapter.addItem(jsonObject)
                    binding.recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                    Log.i("ChatActivity: MsgSucc", text)
                } catch (e: Exception) {
                    Log.e("ChatActivity: MsgFail", e.toString())
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            this@ChatActivity.runOnUiThread {
                Log.e("ChatActivity: Failure", "$t; Response: $response")
            }
        }
    }

    private fun initializeViews() {
        messageAdapter = MessageAdapter(layoutInflater)
        binding.recyclerView.adapter = messageAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun resetEditMessage() {
        binding.messageEdit.setText("")
    }
}