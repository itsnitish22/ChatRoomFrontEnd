package com.nitishsharma.chatapp.chats

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.nitishsharma.chatapp.utils.Utility
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject

class ChatActivityViewModel : ViewModel() {
    private val _receivedData: MutableLiveData<JSONObject?> = MutableLiveData()
    val receivedData: LiveData<JSONObject?>
        get() = _receivedData

    private val _roomError: MutableLiveData<String?> = MutableLiveData()
    val roomError: LiveData<String?>
        get() = _roomError

    private val _roomEvent: MutableLiveData<String?> = MutableLiveData()
    val roomEvent: LiveData<String?>
        get() = _roomEvent

    private val _onUserLeftRoomEvent: MutableLiveData<String> = MutableLiveData()
    val onUserLeftRoomEvent: LiveData<String>
        get() = _onUserLeftRoomEvent


    //socket listeners
    /**
     * below are all [Socket] listeners
     * */

    fun initializeSocketListeners(socketIOInstance: Socket?) {
        socketIOInstance?.on("chat-message", onNewChatMessageEvent)
        socketIOInstance?.on("room-event", onRoomEvent)
        socketIOInstance?.on("room-error", onRoomError)
        socketIOInstance?.on("leave-room-event", onUserLeftRoom)
    }

    private val onNewChatMessageEvent =
        Emitter.Listener { args ->
            _receivedData.postValue(Utility.bundleToJSONMapping(args, null))
        }

    private val onRoomError = Emitter.Listener { args ->
        _roomError.postValue(JSONArray(Gson().toJson(args))[0].toString())
    }

    private val onRoomEvent = Emitter.Listener { args ->
        _roomEvent.postValue(JSONArray(Gson().toJson(args))[0].toString())
    }

    private val onUserLeftRoom = Emitter.Listener { args ->
        _onUserLeftRoomEvent.postValue(JSONArray(Gson().toJson(args))[0].toString())
    }


    //socket emitters
    /**
     * below are all [Socket] emitters
     * */

    fun sendTextMessageEvent(socketIOInstance: Socket?, dataToSend: JSONObject) {
        socketIOInstance?.emit("chat-message", dataToSend)
    }

    fun sendUserLeaveRoomEvent(
        socketIOInstance: Socket?,
        firebaseAuth: FirebaseAuth,
        roomId: String
    ) {
        socketIOInstance?.emit("leave-room", Utility.bundleToJSONMapping(null, Bundle().apply {
            putString("roomId", roomId)
            putString("userId", firebaseAuth.currentUser?.uid)
            putString("userName", firebaseAuth.currentUser?.displayName)
        }))
    }
}