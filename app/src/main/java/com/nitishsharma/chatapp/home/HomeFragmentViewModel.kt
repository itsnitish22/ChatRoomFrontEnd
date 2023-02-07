package com.nitishsharma.chatapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class HomeFragmentViewModel : ViewModel() {
    private val firebaseInstance = FirebaseAuth.getInstance()

    private val _receivedRoomName: MutableLiveData<String?> = MutableLiveData()
    val receivedRoomName: MutableLiveData<String?>
        get() = _receivedRoomName

    private val _successSignOut: MutableLiveData<Boolean> = MutableLiveData()
    val successSignOut: LiveData<Boolean>
        get() = _successSignOut

    fun signOutUser() {
        firebaseInstance.signOut()
        _successSignOut.value = true
    }


    fun createAndJoinRoom(
        socketIOInstance: Socket?,
        firebaseInstance: FirebaseAuth,
        roomName: String
    ): String {
        val roomId = generateUUID()
        val data = mapToJSON2(roomId, roomName)
        socketIOInstance?.emit("create-room", data)
        return joinRoom(socketIOInstance, roomId, firebaseInstance)
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
        jsonObject.put("roomId", roomId)
        jsonObject.put("userName", firebaseInstance.currentUser?.displayName)

        return jsonObject
    }

    private fun mapToJSON2(roomId: String, roomName: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("roomId", roomId)
        jsonObject.put("roomName", roomName)

        return jsonObject
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun initializeSocketListeners(socketIOInstance: Socket?) {
        socketIOInstance?.on("join-room-name", onReceivedRoomName)
    }

    private val onReceivedRoomName =
        Emitter.Listener { args ->
            _receivedRoomName.postValue(JSONArray(Gson().toJson(args))[0].toString())
        }
}