package com.nitishsharma.chatapp.chats

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitishsharma.chatapp.base.BaseActivity
import com.nitishsharma.chatapp.databinding.ActivityChatBinding
import com.nitishsharma.chatapp.utils.Utility.shareRoom
import com.nitishsharma.chatapp.utils.Utility.toast
import org.json.JSONObject
import org.koin.android.ext.android.inject


class ChatActivity : BaseActivity<ActivityChatBinding>() {
    override fun getViewBinding(): ActivityChatBinding = ActivityChatBinding.inflate(layoutInflater)
    private lateinit var userName: String
    private lateinit var roomID: String
    private lateinit var roomName: String
    private lateinit var messageAdapter: MessageAdapter
    private val chatActivityViewModel: ChatActivityViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userName = intent.getStringExtra("userName").toString()
        roomID = intent.getStringExtra("roomID").toString()
        roomName = intent.getStringExtra("roomName").toString()

        //initializing stuff
        initViews()
        initializeRecyclerAdapter()
    }

    override fun initClickListeners() {
        super.initClickListeners()
        binding.sendBtn.setOnClickListener {
            if (socketIOInstance?.connected() == true && binding.messageEdit.text.toString()
                    .isNotEmpty()
            ) {
                sendTextMessageEvent()
            }
        }
        binding.backIv.setOnClickListener {
            finish()
        }
        binding.shareRoomId.setOnClickListener {
            shareRoom(roomID, roomName)
        }
    }

    private fun initViews() {
        binding.roomNameTv.text = roomName
        binding.roomIdTv.text = roomID
    }

    //sending leave room event
    override fun onDestroy() {
        super.onDestroy()
        sendUserLeaveRoomEvent()
    }

    //leave room event function
    private fun sendUserLeaveRoomEvent() {
        chatActivityViewModel.sendUserLeaveRoomEvent(socketIOInstance, firebaseAuth!!, roomID)
    }

    //initializing observers
    override fun initObservers() {
        super.initObservers()
        chatActivityViewModel.receivedData.observe(this, Observer { receivedData ->
            receivedData?.let {
                sendDataToAdapter(receivedData)
            }
        })

        chatActivityViewModel.roomEvent.observe(this, Observer { roomEvent ->
            toast(roomEvent.toString())
        })

        chatActivityViewModel.roomError.observe(this, Observer { roomError ->
            toast(roomError.toString())
        })

        chatActivityViewModel.onUserLeftRoomEvent.observe(this, Observer { userDisconnectEvent ->
            toast(userDisconnectEvent)
        })
    }

    //initializing socket listeners
    override fun initSocketListeners() {
        super.initSocketListeners()
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
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.recyclerView.layoutManager = layoutManager

    }

    //resetting the edit text on msg sent
    private fun resetEditMessage() {
        binding.messageEdit.setText("")
    }

    //convert json from the data (RAW)
    private fun jsonFromData(): JSONObject {
        val jsonData = JSONObject()
        jsonData.put("userName", userName)
        jsonData.put("message", binding.messageEdit.text.toString())
        jsonData.put("roomId", roomID)
        jsonData.put("isSent", true)

        return jsonData
    }
}