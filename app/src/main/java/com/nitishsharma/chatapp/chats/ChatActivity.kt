package com.nitishsharma.chatapp.chats

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitishsharma.chatapp.application.FirstChat
import com.nitishsharma.chatapp.databinding.ActivityChatBinding
import io.socket.client.Socket
import org.json.JSONObject


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var name: String
    private lateinit var roomID: String
    private lateinit var messageAdapter: MessageAdapter
    private val chatActivityViewModel: ChatActivityViewModel by viewModels()
    var socketIOInstance: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing global variables
        socketIOInstance = (application as FirstChat).socketIO
        name = intent.getStringExtra("name").toString()
        roomID = intent.getStringExtra("roomID").toString()

        //initializing stuff
        initializeRecyclerAdapter()
        initializeSocketListeners()
        initializeObservers()

        //click on send button
        binding.sendBtn.setOnClickListener {
            if (socketIOInstance?.connected() == true && binding.messageEdit.text.toString()
                    .isNotEmpty()
            ) {
                sendTextMessageEvent()
            }
        }
    }

    //sending leave room event
    override fun onDestroy() {
        super.onDestroy()
        sendUserLeaveRoomEvent()
    }

    //leave room event function
    private fun sendUserLeaveRoomEvent() {
        val sendingData = jsonFromData()
        chatActivityViewModel.sendUserLeaveRoomEvent(socketIOInstance, sendingData)
    }

    //initializing observers
    private fun initializeObservers() {
        chatActivityViewModel.receivedData.observe(this, Observer { receivedData ->
            receivedData?.let {
                sendDataToAdapter(receivedData)
            }
        })
    }

    //initializing socket listeners
    private fun initializeSocketListeners() {
        chatActivityViewModel.initializeSocketListeners(socketIOInstance)
    }

    //fun to send text message
    private fun sendTextMessageEvent() {
        val sendingData = jsonFromData()
        chatActivityViewModel.sendTextMessageEvent(socketIOInstance, sendingData)
        sendDataToAdapter(sendingData)
        resetEditMessage()
    }

    //sending data to adapter
    private fun sendDataToAdapter(dataToAdapter: JSONObject) {
        messageAdapter.addItem(dataToAdapter)
        binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
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

    //convert json from the data (RAW)
    private fun jsonFromData(): JSONObject {
        val jsonData = JSONObject()
        jsonData.put("name", name)
        jsonData.put("message", binding.messageEdit.text.toString())
        jsonData.put("roomid", roomID)
        jsonData.put("isSent", true)

        return jsonData
    }
}