package com.nitishsharma.chatapp.chats

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitishsharma.chatapp.adapters.MessageAdapter
import com.nitishsharma.chatapp.databinding.ActivityChatBinding
import com.nitishsharma.chatapp.utils.Utility
import okhttp3.*
import org.json.JSONObject


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var name: String
    private lateinit var webSocket: WebSocket
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getting user's name from intent
        name = intent.getStringExtra("name").toString()

        //initializing stuff
        initializeRecyclerAdapter()
        initiateSocketConnection()

        binding.sendBtn.setOnClickListener {
            sendTextMessage()
        }
    }

    //sending text msg to socket server
    private fun sendTextMessage() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", name)
            jsonObject.put("message", binding.messageEdit.text.toString())
            webSocket.send(jsonObject.toString())
            jsonObject.put("isSent", true)
            messageAdapter.addItem(jsonObject)
            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1);
            resetEditMessage()
        } catch (e: Exception) {
            Log.e("Msg Delivery Fail ", e.toString())
        }
    }

    //initiating socket connection
    private fun initiateSocketConnection() {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(Utility.SERVER_PATH).build()
        webSocket = client.newWebSocket(request, SocketListener())
    }

    //initializing recycler adapter
    private fun initializeRecyclerAdapter() {
        messageAdapter = MessageAdapter(layoutInflater)
        binding.recyclerView.adapter = messageAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    //resetting the edit text on msg sent
    private fun resetEditMessage() {
        binding.messageEdit.setText("")
    }

    //socket listener stuff
    inner class SocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            this@ChatActivity.runOnUiThread {
                Toast.makeText(this@ChatActivity, "Connection made", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.i("ChatActivity: MsgSucc", text)
            this@ChatActivity.runOnUiThread {
                try {
                    val jsonObject =
                        JSONObject(text.substring(text.indexOf("{"), text.lastIndexOf("}") + 1))
                    jsonObject.put("isSent", false)
                    messageAdapter.addItem(jsonObject)
                    binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
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

}