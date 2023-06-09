package com.nitishsharma.chatapp.main.chats

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.nitishsharma.chatapp.base.BaseViewModel
import com.nitishsharma.chatapp.utils.Utility
import com.nitishsharma.domain.api.interactors.GetRoomDetailsUseCase
import com.nitishsharma.domain.api.interactors.GetUserAvatarUseCase
import com.nitishsharma.domain.api.models.roomsresponse.ActiveRooms
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ChatActivityViewModel : BaseViewModel(), KoinComponent {
    private val getRoomDetailsUseCase: GetRoomDetailsUseCase by inject()
    private val getUserAvatarUseCase: GetUserAvatarUseCase by inject()

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

    private val _onSomeoneJoinedRoomEvent: MutableLiveData<String> = MutableLiveData()
    val onSomeoneJoinedRoomEvent: LiveData<String>
        get() = _onSomeoneJoinedRoomEvent

    private val _roomDetails: MutableLiveData<ActiveRooms> = MutableLiveData()
    val roomDetails: LiveData<ActiveRooms>
        get() = _roomDetails

    private val _mapOfCreatorAndJoinerAvatar: MutableLiveData<Pair<String?, String?>> =
        MutableLiveData()
    val mapOfCreatorAndJoinerAvatar: LiveData<Pair<String?, String?>>
        get() = _mapOfCreatorAndJoinerAvatar


    //socket listeners
    /**
     * below are all [Socket] listeners
     * */

    fun initializeSocketListeners(socketIOInstance: Socket?) {
        socketIOInstance?.on("chat-message", onNewChatMessageEvent)
        socketIOInstance?.on("room-event", onRoomEvent)
        socketIOInstance?.on("room-error", onRoomError)
        socketIOInstance?.on("leave-room-event", onUserLeftRoom)
        socketIOInstance?.on("join-room-event", onSomeoneJoinedRoom)
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
    private val onSomeoneJoinedRoom = Emitter.Listener { args ->
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
            putBoolean("isFree", true)
        }))
    }

    fun sendSomeoneJoinedRoomEvent(
        socketIOInstance: Socket?,
        firebaseAuth: FirebaseAuth?,
        roomId: String
    ) {
        socketIOInstance?.emit(
            "user-joined-room",
            Utility.bundleToJSONMapping(null, Bundle().apply {
                putString("roomId", roomId)
                putString("userName", firebaseAuth?.currentUser?.displayName)
            })
        )
    }

    fun getRoomDetailsFromRoomId(roomId: String) {
        viewModelScope.launch {
            try {
                val response = getRoomDetailsUseCase.invoke(roomId)
                if (response.isSuccessful) {
                    if (response.body()?.activeRooms?.size!! >= 1)
                        _roomDetails.postValue(response.body()?.activeRooms?.get(0)!!)
                }
            } catch (e: Exception) {
                Timber.tag("Active Rooms Error").e(e.toString())
            }
        }
    }

    fun getCreatorAndJoinerAvatarUrls(creatorId: String, joinerId: String?) {
        viewModelScope.launch {
            try {
                val creatorAvatar = getUserAvatarUseCase.invoke(creatorId)
                val joinerAvatar = joinerId?.let { getUserAvatarUseCase.invoke(it) }

                val mapOfCreatorAndJoiner = Pair<String?, String?>(
                    creatorAvatar.body()?.userAvatar,
                    joinerAvatar?.body()?.userAvatar
                )
                _mapOfCreatorAndJoinerAvatar.postValue(mapOfCreatorAndJoiner)
            } catch (e: Exception) {
                Timber.tag("Avtar Error").e(e.toString())
            }
        }
    }
}