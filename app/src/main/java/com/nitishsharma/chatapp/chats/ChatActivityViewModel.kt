package com.nitishsharma.chatapp.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.nitishsharma.chatapp.models.chatresponse.parseMessage
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


    //socket listeners
    /**
     * below are all [Socket] listeners
     * */

    fun initializeSocketListeners(socketIOInstance: Socket?) {
        socketIOInstance?.on("chat-message", onNewChatMessageEvent)
        socketIOInstance?.on("room-event", onRoomEvent)
        socketIOInstance?.on("room-error", onRoomError)
    }

    private val onNewChatMessageEvent =
        Emitter.Listener { args ->
            val dataFromUser = dataToJson(args)
            _receivedData.postValue(null)
            _receivedData.postValue(dataFromUser)
        }

    private val onRoomError = Emitter.Listener { args ->
        _roomError.postValue(null)
        _roomError.postValue(JSONArray(Gson().toJson(args))[0].toString())
    }

    private val onRoomEvent = Emitter.Listener { args ->
        _roomEvent.postValue(null)
        _roomEvent.postValue(JSONArray(Gson().toJson(args))[0].toString())
    }


    //socket emitters
    /**
     * below are all [Socket] emitters
     * */

    fun sendTextMessageEvent(socketIOInstance: Socket?, dataToSend: JSONObject) {
        socketIOInstance?.emit("chat-message", dataToSend)
    }

    fun sendUserLeaveRoomEvent(socketIOInstance: Socket?, dataToSend: JSONObject) {
        socketIOInstance?.emit("leave-room", dataToSend)
    }


    //utility functions
    /**
     * below are all Utility functions
     * */

    private fun dataToJson(args: Array<Any>?): JSONObject {
        val receivedMessageFromServer =
            parseMessage(JSONArray(Gson().toJson(args))[0].toString())

        val mappedData = JSONObject()
        mappedData.put("userName", receivedMessageFromServer.userName)
        mappedData.put("message", receivedMessageFromServer.message)
        mappedData.put("roomId", receivedMessageFromServer.roomId)
        mappedData.put("isSent", false)

        return mappedData
    }
}