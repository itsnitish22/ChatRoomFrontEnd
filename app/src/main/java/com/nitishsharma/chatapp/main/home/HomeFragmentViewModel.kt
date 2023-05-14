package com.nitishsharma.chatapp.main.home

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.nitishsharma.chatapp.base.BaseViewModel
import com.nitishsharma.chatapp.utils.Utility
import com.nitishsharma.domain.api.interactors.CanJoinRoomUseCase
import com.nitishsharma.domain.api.interactors.DeleteCurrentRoomUseCase
import com.nitishsharma.domain.api.interactors.GetAllActiveRoomsUseCase
import com.nitishsharma.domain.api.interactors.UpdateRoomAvailableStatusUseCase
import com.nitishsharma.domain.api.interactors.UpdateRoomJoinerIdUseCase
import com.nitishsharma.domain.api.models.canjoinroom.CanJoinRoom
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.koin.core.component.inject
import timber.log.Timber

class HomeFragmentViewModel : BaseViewModel() {
    private val getALlUserActiveRoomsUseCase: GetAllActiveRoomsUseCase by inject()
    private val deleteCurrentRoomUseCase: DeleteCurrentRoomUseCase by inject()
    private val checkIfCanJoinRoomUseCase: CanJoinRoomUseCase by inject()
    private val updateRoomAvailableStatusUseCase: UpdateRoomAvailableStatusUseCase by inject()
    private val updateRoomJoinerIdUseCase: UpdateRoomJoinerIdUseCase by inject()

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

    private val _canJoinRoom: MutableLiveData<CanJoinRoom> = MutableLiveData()
    val canJoinRoom: LiveData<CanJoinRoom>
        get() = _canJoinRoom

    private val _changedRoomAvailableStatus: MutableLiveData<Boolean?> = MutableLiveData(null)
    val changedRoomAvailableStatus: LiveData<Boolean?>
        get() = _changedRoomAvailableStatus

    private val _updatedRoomJoinerId: MutableLiveData<Boolean?> = MutableLiveData(null)
    val updatedRoomJoinerId: LiveData<Boolean?>
        get() = _updatedRoomJoinerId

    fun signOutUser() {
        firebaseInstance.signOut()
        _successSignOut.postValue(true)
    }

    fun getAllUserActiveRooms(body: AllUserActiveRoomsBody) {
        viewModelScope.launch {
            try {
                val response = getALlUserActiveRoomsUseCase.invoke(body)
                if (response.isSuccessful)
                    _responseAllUserActiveRooms.postValue(response.body())
            } catch (e: Exception) {
                Timber.tag("Active Rooms Error").e(e.toString())
            }
        }
    }

    fun deleteCurrentRoom(roomId: String) {
        viewModelScope.launch {
            try {
                val response = deleteCurrentRoomUseCase.invoke(
                    Utility.bundleToJSONMapping(null,
                        Bundle().apply {
                            putString("roomId", roomId)
                            putString("userId", firebaseInstance.currentUser?.uid)
                            putBoolean("increaseRoomCount", false)
                        })
                )
                if (response.isSuccessful)
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
        socketIOInstance?.emit("create-room", Utility.bundleToJSONMapping(null,
            Bundle().apply {
                putString("roomId", roomId);
                putString("userId", firebaseInstance.currentUser?.uid); //in db it is creator_id
                putString("joinerId", null)
                putString("roomName", roomName)
                putBoolean("increaseRoomCount", true)
            }
        ))
        return joinRoom(socketIOInstance, roomId, firebaseInstance)
    }

    fun joinRoom(
        socketIOInstance: Socket?,
        roomId: String,
        firebaseInstance: FirebaseAuth
    ): String {
        socketIOInstance?.emit(
            "join-room", Utility.bundleToJSONMapping(
                null, Bundle().apply {
                    putString("roomId", roomId);
                    putString("userName", firebaseInstance.currentUser?.displayName)
                    putString("userId", firebaseInstance.currentUser?.uid)
                    putBoolean("isFree", false)
                })
        )
        return roomId
    }

    fun checkIfCanJoinRoom(firebaseInstance: FirebaseAuth, roomId: String) {
        viewModelScope.launch {
            try {
                _canJoinRoom.postValue(firebaseInstance.currentUser?.let {
                    checkIfCanJoinRoomUseCase.invoke(
                        it.uid, roomId
                    )
                })
            } catch (e: java.lang.Exception) {
                Timber.tag("Check If Can Join Room Error").e(e.toString())
            }
        }
    }

    fun updateRoomIsAvailableStatus(isAvailable: Boolean, roomId: String) {
        viewModelScope.launch {
            try {
                val response = updateRoomAvailableStatusUseCase.invoke(isAvailable, roomId)
                if (response.isSuccessful)
                    _changedRoomAvailableStatus.postValue(true)
                else
                    _changedRoomAvailableStatus.postValue(false)
            } catch (e: java.lang.Exception) {
                Timber.tag("Update Room Is Available Error").e(e.toString())
            }
        }
    }

    fun updateRoomJoinerId(userId: String?, roomId: String) {
        viewModelScope.launch {
            try {
                val response = updateRoomJoinerIdUseCase.invoke(userId, roomId)
                if (response.isSuccessful)
                    _updatedRoomJoinerId.postValue(true)
            } catch (e: Exception) {
                Timber.tag("Update Room Joiner Error").e(e.toString())
            }
        }
    }

    fun initializeSocketListeners(socketIOInstance: Socket?) {
        socketIOInstance?.on("join-room-name", onReceivedRoomName)
    }

    private val onReceivedRoomName =
        Emitter.Listener { args ->
            _receivedRoomName.postValue(JSONArray(Gson().toJson(args))[0].toString())
        }
}