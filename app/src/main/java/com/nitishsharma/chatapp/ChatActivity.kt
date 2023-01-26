package com.nitishsharma.chatapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitishsharma.chatapp.databinding.ActivityChatBinding
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


class ChatActivity : AppCompatActivity(), TextWatcher {
    private lateinit var binding: ActivityChatBinding
    private lateinit var name: String
    private lateinit var webSocket: WebSocket
    private var SERVER_PATH =
        "wss://s8293.blr1.piesocket.com/v3/1?api_key=GoHXyxyEaNrhLpxXbs2oFtog93M7OiS7Q2TTgPWf&notify_self=1"
    private val IMAGE_REQUEST_ID = 1
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

        binding.pickImgBtn.setOnClickListener {
            pickAndSendImage()
        }
    }

    private fun pickAndSendImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Pick Image"), IMAGE_REQUEST_ID)
    }

    private fun sendTextMessage() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", name)
            jsonObject.put("message", binding.messageEdit.text.toString())
            webSocket.send(jsonObject.toString())
            jsonObject.put("isSent", true)
            messageAdapter.addItem(jsonObject)
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
        binding.messageEdit.addTextChangedListener(this)
        messageAdapter = MessageAdapter(layoutInflater)
        binding.recyclerView.adapter = messageAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_ID && resultCode == RESULT_OK) {
            try {
                val inputStream = data?.data?.let { contentResolver.openInputStream(it) }
                val image = BitmapFactory.decodeStream(inputStream)
                sendImage(image)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun sendImage(image: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val base64String: String = Base64.encodeToString(
            outputStream.toByteArray(),
            Base64.DEFAULT
        )
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", name)
            jsonObject.put("image", base64String)
            webSocket.send(jsonObject.toString())
            jsonObject.put("isSent", true)
            messageAdapter.addItem(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
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