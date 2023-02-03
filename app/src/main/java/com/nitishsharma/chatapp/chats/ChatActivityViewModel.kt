package com.nitishsharma.chatapp.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.nitishsharma.chatapp.models.parseMessage
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject

class ChatActivityViewModel : ViewModel() {
    private val _receivedData: MutableLiveData<JSONObject?> = MutableLiveData()
    val receivedData: LiveData<JSONObject?>
        get() = _receivedData

    fun initializeSocketListeners(socketIOInstance: Socket?) {
        socketIOInstance?.on("chat-message", onNewChatMessage)
    }

    fun sendTextMessage(socketIOInstance: Socket?, dataToSend: JSONObject) {
        socketIOInstance?.emit("chat-message", dataToSend)
    }

    private fun dataToJson(args: Array<Any>?): JSONObject {
        val receivedMessageFromServer =
            parseMessage(JSONArray(Gson().toJson(args))[0].toString())

        val mappedData = JSONObject()
        mappedData.put("name", receivedMessageFromServer.name)
        mappedData.put("message", receivedMessageFromServer.message)
        mappedData.put("roomid", receivedMessageFromServer.roomid)
        mappedData.put("isSent", false)

        return mappedData
    }

    private val onNewChatMessage =
        Emitter.Listener { args ->
            val dataFromUser = dataToJson(args)
            _receivedData.postValue(null)
            _receivedData.postValue(dataFromUser)
        }
}