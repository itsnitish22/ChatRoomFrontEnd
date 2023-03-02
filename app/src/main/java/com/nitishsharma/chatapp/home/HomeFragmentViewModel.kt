package com.nitishsharma.chatapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.nitishsharma.chatapp.api.retrofit.RetrofitInstance
import com.nitishsharma.chatapp.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.chatapp.models.roomsresponse.AllUserActiveRoomsBody
import com.nitishsharma.chatapp.utils.Utility
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch
import org.json.JSONArray
import timber.log.Timber

class HomeFragmentViewModel : ViewModel() {
    private val firebaseInstance = FirebaseAuth.getInstance()

    private val _receivedRoomName: MutableLiveData<String?> = MutableLiveData()
    val receivedRoomName: MutableLiveData<String?>
        get() = _receivedRoomName

    private val _successSignOut: MutableLiveData<Boolean> = MutableLiveData(false)
    val successSignOut: LiveData<Boolean>
        get() = _successSignOut

    private val _deleteRoomSuccess: MutableLiveData<Boolean> = MutableLiveData(false)
    val deleteRoomSuccess: LiveData<Boolean>
        get() = _deleteRoomSuccess

    private val _responseAllUserActiveRooms: MutableLiveData<AllUserActiveRooms> = MutableLiveData()
    val responseAllUserActiveRooms: LiveData<AllUserActiveRooms>
        get() = _responseAllUserActiveRooms

    fun signOutUser() {
        firebaseInstance.signOut()
        _successSignOut.postValue(true)
    }

    fun getAllUserActiveRooms(body: AllUserActiveRoomsBody) {
        viewModelScope.launch {
            try {
                _responseAllUserActiveRooms.postValue(RetrofitInstance.api.getAllActiveRooms(body))
            } catch (e: Exception) {
                Timber.tag("Active Rooms Error").e(e.toString())
            }
        }
    }

    fun deleteCurrentRoom(roomId: String) {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.deleteCurrentRoom(Utility.deleteRoomJSONMapping(roomId))
                _deleteRoomSuccess.postValue(true)
            } catch (e: Exception) {
                Timber.tag("Delete Room Error").e(e.toString())
            }
        }
    }

    fun createAndJoinRoom(
        socketIOInstance: Socket?,
        firebaseInstance: FirebaseAuth,
        roomName: String
    ): String {
        val roomId = Utility.generateUUID()
        val data =
            Utility.createRoomJSONMapping(firebaseInstance.currentUser!!.uid, roomId, roomName)
        socketIOInstance?.emit("create-room", data)
        return joinRoom(socketIOInstance, roomId, firebaseInstance)
    }

    fun joinRoom(
        socketIOInstance: Socket?,
        roomId: String,
        firebaseInstance: FirebaseAuth
    ): String {
        val dataToSend = Utility.joinRoomJSONMapping(roomId, firebaseInstance)
        socketIOInstance?.emit("join-room", dataToSend)

        return roomId
    }

    fun initializeSocketListeners(socketIOInstance: Socket?) {
        socketIOInstance?.on("join-room-name", onReceivedRoomName)
    }

    private val onReceivedRoomName =
        Emitter.Listener { args ->
            _receivedRoomName.postValue(JSONArray(Gson().toJson(args))[0].toString())
        }
}