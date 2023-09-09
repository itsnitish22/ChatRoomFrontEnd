package com.nitishsharma.chatapp.main.chats

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.base.BaseActivity
import com.nitishsharma.chatapp.base.common.LoadingModel
import com.nitishsharma.chatapp.databinding.ActivityChatBinding
import com.nitishsharma.chatapp.main.ui.utils.GroupAvatar2
import com.nitishsharma.chatapp.utils.Utility
import com.nitishsharma.chatapp.utils.observeOnce
import com.nitishsharma.chatapp.utils.setStatusBarColor
import com.nitishsharma.chatapp.utils.setVisibilityBasedOnLoadingModel
import com.nitishsharma.chatapp.utils.shareRoom
import com.nitishsharma.chatapp.utils.toast
import com.nitishsharma.domain.api.interactors.IsChatActivityOpenUseCase
import com.nitishsharma.domain.api.models.roomsresponse.ActiveRooms
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class ChatActivity : BaseActivity<ActivityChatBinding>(), KoinComponent {
    override fun getViewBinding(): ActivityChatBinding = ActivityChatBinding.inflate(layoutInflater)
    private lateinit var userName: String
    private lateinit var roomID: String
    private lateinit var roomName: String
    var resetEditText = false
    private lateinit var messageAdapter: MessageAdapter
    var currentChatRooomDetails: ActiveRooms? = null
    private val chatActivityViewModel: ChatActivityViewModel by inject()
    private val isChatActivityOpenUseCase: IsChatActivityOpenUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        extractIntentData()
        initUi()
    }

    private fun initUi() {
        setStatusBarColor(this, R.color.dark_gray)
        chatActivityViewModel.getRoomDetailsFromRoomId(roomID)
        isChatActivityOpenUseCase.setChatActivityOpen(true)
        chatActivityViewModel.getAllChats(roomID)
        initChatTopUi()
        initializeRecyclerAdapter()
    }

    private fun extractIntentData() {
        userName = intent.getStringExtra("userName").toString()
        roomID = intent.getStringExtra("roomID").toString()
        roomName = intent.getStringExtra("roomName").toString()
    }

    override fun onStart() {
        super.onStart()
        sendSomeoneJoinedRoomEvent()
    }

    private fun sendSomeoneJoinedRoomEvent() {
        chatActivityViewModel.sendSomeoneJoinedRoomEvent(socketIOInstance, firebaseAuth, roomID)
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

    override fun onDestroy() {
        super.onDestroy()
        sendUserLeaveRoomEvent()
        isChatActivityOpenUseCase.setChatActivityOpen(false)
    }

    private fun sendUserLeaveRoomEvent() {
        chatActivityViewModel.sendUserLeaveRoomEvent(socketIOInstance, firebaseAuth!!, roomID)
    }

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
        chatActivityViewModel.onSomeoneJoinedRoomEvent.observe(
            this,
            Observer { someOneJoinedEvent ->
                someOneJoinedEvent.getContentIfNotHandled()?.let {
                    toast(it)
                    Handler().postDelayed({
                        chatActivityViewModel.getRoomDetailsFromRoomId(roomID)
                    }, 2000)
                }
            })

        chatActivityViewModel.onUserLeftRoomEvent.observe(this, Observer { userDisconnectEvent ->
            toast(userDisconnectEvent)
            Handler().postDelayed({
                chatActivityViewModel.getRoomDetailsFromRoomId(roomID)
            }, 2000)
        })

        chatActivityViewModel.roomDetails.observe(this, Observer { roomDetails ->
            currentChatRooomDetails = roomDetails
            getCreatorAndJoinerAvatar(roomDetails.creatorId, roomDetails.joinerId)
        })
        chatActivityViewModel.chatData.observeOnce(this, Observer { chatMessages ->
            for (messages in chatMessages) {
                sendDataToAdapter(
                    chatActivityViewModel.messageToJson(
                        messages.userId, messages.userName, messages.message
                    )
                )
            }
        })
        chatActivityViewModel.loadingModel.observe(this, Observer {
            binding.loadingModel.progressBar.setVisibilityBasedOnLoadingModel(it)
        })
    }

    private fun initChatTopUi() {
        binding.composeView.setContent {
            ChatTopView()
        }
    }

    private fun getCreatorAndJoinerAvatar(creatorId: String, joinerId: String?) {
        chatActivityViewModel.getCreatorAndJoinerAvatarUrls(creatorId, joinerId)
    }

    override fun initSocketListeners() {
        super.initSocketListeners()
        chatActivityViewModel.initializeSocketListeners(socketIOInstance)
    }

    private fun sendTextMessageEvent() {
        val sendingData = jsonFromData()
        chatActivityViewModel.sendTextMessageEvent(socketIOInstance, sendingData)
        sendDataToAdapter(sendingData)
        chatActivityViewModel.saveChatInDb(
            roomID,
            firebaseAuth?.currentUser?.uid!!,
            userName,
            Utility.getCurrentTimeStamp(),
            binding.messageEdit.text?.trim().toString()
        )
        resetEditMessage()
    }

    private fun sendDataToAdapter(dataToAdapter: JSONObject) {
        messageAdapter.addItem(dataToAdapter)
        binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
    }

    private fun initializeRecyclerAdapter() {
        messageAdapter = MessageAdapter(layoutInflater)
        binding.recyclerView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.recyclerView.layoutManager = layoutManager

    }

    private fun resetEditMessage() {
        resetEditText = true
        binding.messageEdit.setText("")
    }

    private fun jsonFromData(): JSONObject {
        val jsonData = JSONObject()
        jsonData.put("userName", userName)
        jsonData.put("message", binding.messageEdit.text.toString())
        jsonData.put("roomId", roomID)
        jsonData.put("isSent", true)

        return jsonData
    }

    @Composable
    fun ChatTopView() {
        val pairOfCreatorAndJoiner =
            chatActivityViewModel.mapOfCreatorAndJoinerAvatar.observeAsState()
        val currentJoinedRoom = chatActivityViewModel.roomDetails.observeAsState()
        val scrollAnimatable = remember { Animatable(0f) }
        val text =
            "${currentJoinedRoom.value?.creatorName}${currentJoinedRoom.value?.joinerName?.let { " - $it" } ?: ""}"

        LaunchedEffect(Unit) {
            scrollAnimatable.animateTo(
                targetValue = -text.length * 10f, animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000, easing = LinearEasing
                    )
                )
            )
        }
        val scrollOffset = scrollAnimatable.value

        ConstraintLayout {
            val (groupAvatar, roomNameCv, namesCv) = createRefs()
            GroupAvatar2(
                modifier = Modifier.constrainAs(groupAvatar) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                },
                avatarSize = 30.dp,
                creatorAvatarUrl = pairOfCreatorAndJoiner.value?.first ?: "",
                joinerAvatarUrl = pairOfCreatorAndJoiner.value?.second
            )
            Text(text = currentJoinedRoom.value?.roomName ?: "Your Room",
                color = Color.White,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.sans_bold)), fontSize = 18.sp
                ),
                modifier = Modifier.constrainAs(roomNameCv) {
                    start.linkTo(groupAvatar.end, 5.dp)
                    top.linkTo(groupAvatar.top)
                    bottom.linkTo(namesCv.top)
                })
            Text(text = text, color = Color.White, style = TextStyle(
                fontFamily = FontFamily(Font(R.font.sans_med)), fontSize = 12.sp
            ), modifier = Modifier.constrainAs(namesCv) {
                start.linkTo(groupAvatar.end, 5.dp)
                bottom.linkTo(groupAvatar.bottom)
                top.linkTo(roomNameCv.bottom)
            })
        }
    }
}