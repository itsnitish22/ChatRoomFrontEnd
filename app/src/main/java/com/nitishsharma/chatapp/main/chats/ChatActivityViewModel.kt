package com.nitishsharma.chatapp.main.chats

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.nitishsharma.chatapp.base.BaseViewModel
import com.nitishsharma.chatapp.base.common.model.LoadingModel
import com.nitishsharma.chatapp.main.chats.models.ChatMessage
import com.nitishsharma.chatapp.utils.Event
import com.nitishsharma.chatapp.utils.Utility
import com.nitishsharma.domain.api.interactors.GetRoomDetailsUseCase
import com.nitishsharma.domain.api.interactors.GetUserAvatarUseCase
import com.nitishsharma.domain.api.models.chatresponse.parseMessageTyping
import com.nitishsharma.domain.api.models.roomsresponse.ActiveRooms
import com.nitishsharma.domain.api.utility.Utils
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

    private val _chatData: MutableLiveData<ArrayList<ChatMessage>> = MutableLiveData()
    val chatData: LiveData<ArrayList<ChatMessage>>
        get() = _chatData

    private val _roomError: MutableLiveData<String?> = MutableLiveData()
    val roomError: LiveData<String?>
        get() = _roomError

    private val _roomEvent: MutableLiveData<String?> = MutableLiveData()
    val roomEvent: LiveData<String?>
        get() = _roomEvent

    private val _onUserLeftRoomEvent: MutableLiveData<String> = MutableLiveData()
    val onUserLeftRoomEvent: LiveData<String>
        get() = _onUserLeftRoomEvent

    private val _onSomeoneJoinedRoomEvent: MutableLiveData<Event<String>> = MutableLiveData()
    val onSomeoneJoinedRoomEvent: LiveData<Event<String>>
        get() = _onSomeoneJoinedRoomEvent

    private val _roomDetails: MutableLiveData<ActiveRooms> = MutableLiveData()
    val roomDetails: LiveData<ActiveRooms>
        get() = _roomDetails

    private val _mapOfCreatorAndJoinerAvatar: MutableLiveData<Pair<String?, String?>> =
        MutableLiveData()
    val mapOfCreatorAndJoinerAvatar: LiveData<Pair<String?, String?>>
        get() = _mapOfCreatorAndJoinerAvatar

    private val _onUserTypingEvent: MutableLiveData<JSONObject> =
        MutableLiveData()
    val onUserTypingEvent: LiveData<JSONObject>
        get() = _onUserTypingEvent

    private val _onUserTypingStopEvent: MutableLiveData<Boolean> =
        MutableLiveData()
    val onUserTypingStopEvent: LiveData<Boolean>
        get() = _onUserTypingStopEvent


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
        socketIOInstance?.on("typing-event", onUserTyping)
        socketIOInstance?.on("user-typing-stop-event", onUserTypingStop)
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
        _onSomeoneJoinedRoomEvent.postValue(Event(JSONArray(Gson().toJson(args))[0].toString()))
    }

    private val onUserTyping = Emitter.Listener { args ->
        _onUserTypingEvent.postValue(onUserTypingDataConversion(args))
    }

    private val onUserTypingStop = Emitter.Listener { args ->
        _onUserTypingStopEvent.postValue(true)
    }

    private fun onUserTypingDataConversion(args: Array<Any>? = null): JSONObject {
        val json = JSONObject()
        val receivedMessageFromServer =
            parseMessageTyping(JSONArray(Gson().toJson(args))[0].toString())
        json.put("roomId", receivedMessageFromServer.roomId)
        json.put("userName", receivedMessageFromServer.userName)
        json.put("isSent", receivedMessageFromServer.isSent)
        json.put("typing", receivedMessageFromServer.typing)
        json.put("showTyping", receivedMessageFromServer.showTyping)
        return json
    }


    //socket emitters
    /**
     * below are all [Socket] emitters
     * */

    fun sendTextMessageEvent(socketIOInstance: Socket?, dataToSend: JSONObject) {
        socketIOInstance?.emit("chat-message", dataToSend)
    }

    fun sendUserTypingEventStop(socketIOInstance: Socket?, roomId: String) {
        socketIOInstance?.emit("user-typing-stop", Utils.bundleToJSONMapping(null, Bundle().apply {
            putString("roomId", roomId)
        }))
    }

    fun sendUserTypingEvent(
        socketIOInstance: Socket?,
        firebaseAuth: FirebaseAuth,
        roomId: String,
        showTyping: Boolean
    ) {
        socketIOInstance?.emit("user-typing", Utils.bundleToJSONMapping(null, Bundle().apply {
            putString("roomId", roomId)
            putString("userName", firebaseAuth?.currentUser?.displayName)
            putBoolean("isSent", false)
            putBoolean("typing", true)
            putBoolean("showTyping", showTyping)
        }))
    }

    fun sendUserLeaveRoomEvent(
        socketIOInstance: Socket?,
        firebaseAuth: FirebaseAuth,
        roomId: String
    ) {
        socketIOInstance?.emit("leave-room", JSONObject().apply {
            put("roomId", roomId)
            put("userId", firebaseAuth.currentUser?.uid)
            put("userName", firebaseAuth.currentUser?.displayName)
            put("isFree", true)
        })
    }

    fun sendSomeoneJoinedRoomEvent(
        socketIOInstance: Socket?,
        firebaseAuth: FirebaseAuth?,
        roomId: String
    ) {
        socketIOInstance?.emit(
            "user-joined-room",
            Utils.bundleToJSONMapping(null, Bundle().apply {
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

    fun getAllChats(roomId: String) {
        try {
            updateLoadingModel(LoadingModel.LOADING)
            val messagesReference = firebaseDb.getReference("rooms").child(roomId).child("messages")
            messagesReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = mutableListOf<ChatMessage>()

                    for (childSnapshot in snapshot.children) {
                        val chatMessage = childSnapshot.getValue(ChatMessage::class.java)
                        chatMessage?.let {
                            chatList.add(it)
                        }
                    }
                    _chatData.postValue(chatList as ArrayList<ChatMessage>)
                    updateLoadingModel(LoadingModel.COMPLETED)
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.message)
                    updateLoadingModel(LoadingModel.ERROR)
                }
            })
        } catch (e: Exception) {
            updateLoadingModel(LoadingModel.ERROR)
            Timber.e(e.message.toString())
        }
    }


    fun saveChatInDb(
        roomId: String,
        userId: String,
        userName: String,
        timeStamp: String,
        msg: String
    ) {
        val chatMessage = ChatMessage(userId, userName, timeStamp, msg)
        firebaseDb.getReference("rooms").child(roomId).child("messages").push()
            .setValue(chatMessage).addOnFailureListener {
                Timber.e(it.message.toString())
            }
    }

    fun messageToJson(userId: String?, userName: String?, message: String?): JSONObject {
        val jsonData = JSONObject()
        jsonData.put("userName", userName)
        jsonData.put("message", message)
        jsonData.put("isSent", userId == firebaseInstance.currentUser?.uid)
        return jsonData
    }
}