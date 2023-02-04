package com.nitishsharma.chatapp.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import io.socket.client.Socket
import org.json.JSONObject
import java.util.*

class HomeFragmentViewModel : ViewModel() {

    fun createAndJoinRoom(socketIOInstance: Socket?, firebaseInstance: FirebaseAuth): String {
        val roomId = generateUUID()
        socketIOInstance?.emit("create-room", roomId)
        joinRoom(socketIOInstance, roomId, firebaseInstance)

        return roomId
    }

    fun joinRoom(
        socketIOInstance: Socket?,
        roomId: String,
        firebaseInstance: FirebaseAuth
    ): String {
        val dataToSend = mapToJSON(roomId, firebaseInstance)
        socketIOInstance?.emit("join-room", dataToSend)

        return roomId
    }


    private fun mapToJSON(roomId: String, firebaseInstance: FirebaseAuth): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("roomid", roomId)
        jsonObject.put("name", firebaseInstance.currentUser?.displayName)

        return jsonObject
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}