package com.nitishsharma.chatapp.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.base.BaseFragment
import com.nitishsharma.chatapp.databinding.FragmentHomeBinding
import com.nitishsharma.chatapp.main.chats.ChatActivity
import com.nitishsharma.chatapp.main.home.ui.components.AppName
import com.nitishsharma.chatapp.main.home.ui.components.DrawerItem
import com.nitishsharma.chatapp.main.home.ui.components.FloatingActionMenu
import com.nitishsharma.chatapp.main.home.ui.components.HomeScreenRoomItem
import com.nitishsharma.chatapp.main.home.ui.components.MiddleNoActiveRooms
import com.nitishsharma.chatapp.main.home.ui.components.RandomButton
import com.nitishsharma.chatapp.main.home.ui.components.ShimmerItem
import com.nitishsharma.chatapp.main.ui.theme.AppTheme
import com.nitishsharma.chatapp.main.ui.utils.Avatar
import com.nitishsharma.chatapp.utils.Utility.setStatusBarColor
import com.nitishsharma.chatapp.utils.Utility.toast
import com.nitishsharma.domain.api.models.roomsresponse.ActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.ConvertToBodyForAllUserActiveRooms
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding>(),
    RoomUtilityBottomSheet.RoomUtilityCallback {
    override fun getViewBinding() = FragmentHomeBinding.inflate(layoutInflater)
    private val homeFragmentArgs: HomeFragmentArgs by navArgs()
    private val homeFragmentVM: HomeFragmentViewModel by activityViewModels()
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var roomId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(requireActivity(), R.color.app_bg)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getAllUserActiveRooms()
        super.onViewCreated(view, savedInstanceState)
        bottomSheetDialog = BottomSheetDialog(requireContext())
    }

    private fun getAllUserActiveRooms() {
        homeFragmentVM.getAllUserActiveRooms(
            ConvertToBodyForAllUserActiveRooms.convert(
                firebaseInstance.currentUser!!.uid
            )
        )
    }

    override fun initComposeView() {
        super.initComposeView()
        binding.composeView.setContent {
            HomeScreenUI(
            )
        }
    }

    override fun initClickListeners() {
        binding.apply {
            binding.swipeRefresh.setOnRefreshListener {
                getAllUserActiveRooms()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllUserActiveRooms()
    }

    override fun initObservers() {
        homeFragmentVM.receivedRoomName.observe(viewLifecycleOwner, Observer { receivedName ->
            roomId?.let {
                startChatActivity(it, receivedName.toString())
            }
        })

        homeFragmentVM.successSignOut.observe(viewLifecycleOwner, Observer { signOut ->
            signOut?.let { eventSignOut ->
                eventSignOut.getContentIfNotHandled()?.let { beginSignOut ->
                    if (beginSignOut)
                        navigateToOnboardingFragment()
                }
            }
        })

        homeFragmentVM.responseAllUserActiveRoomsWithJoinerAvatar.observe(
            viewLifecycleOwner,
            Observer { mappedRomsWithJoinerAvatar ->
                if (binding.swipeRefresh.isRefreshing)
                    binding.swipeRefresh.isRefreshing = false
            })

        homeFragmentVM.deleteRoomResponse.observe(
            viewLifecycleOwner,
            Observer { deleteRoomResponse ->
                if (deleteRoomResponse.deletedRoom) {
                    getAllUserActiveRooms()
                } else {
                    toast(deleteRoomResponse.message)
                }
            })

        homeFragmentVM.canJoinRoom.observe(viewLifecycleOwner, Observer { canJoinRoom ->
            if (canJoinRoom.ownRoom) {
                roomId?.let {
                    joinChatRoom(it)
                }
            } else {
                if (canJoinRoom.canJoin) {
                    roomId?.let {
                        updateRoomIsAvailableStatus(false, it)
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    toast(canJoinRoom.actionForUser)
                }
            }
        })

        homeFragmentVM.changedRoomAvailableStatus.observe(
            viewLifecycleOwner,
            Observer { changedRoomAvailableStatus ->
                changedRoomAvailableStatus?.let {
                    if (it) {
                        roomId?.let {
                            updateRoomJoinerId(firebaseInstance.currentUser?.uid, it)
                        }
                    } else {
                        toast("room is not available")
                    }
                }
            })

        homeFragmentVM.updatedRoomJoinerId.observe(
            viewLifecycleOwner,
            Observer { updatedRoomJoinerId ->
                updatedRoomJoinerId?.let {
                    roomId?.let {
                        homeFragmentVM.addRoomToOtherRoomsArray(
                            firebaseInstance.currentUser?.uid,
                            it
                        )
                    }
                }
            })

        homeFragmentVM.addRoomToOtherRoomsArraySuccess.observe(
            viewLifecycleOwner,
            Observer { addRoomToArraySuccess ->
                if (addRoomToArraySuccess) {
                    roomId?.let {
                        joinChatRoom(it)
                    }
                }
            })

        homeFragmentVM.serverError.observe(viewLifecycleOwner, Observer { error ->
            if (error) {
                toast("Internal Server Error")
                if (binding.swipeRefresh.isRefreshing)
                    binding.swipeRefresh.isRefreshing = false
            }
        })
    }

    private fun updateRoomJoinerId(uid: String?, roomId: String) {
        homeFragmentVM.updateRoomJoinerId(uid, roomId, firebaseInstance.currentUser?.displayName)
    }

    private fun updateRoomIsAvailableStatus(isRoomAvailable: Boolean, roomId: String) {
        homeFragmentVM.updateRoomIsAvailableStatus(isRoomAvailable, roomId)
    }

    private fun showRoomOptionsBottomSheet(currentRoom: ActiveRooms) {
        RoomOptionsBottomSheet.newInstance(currentRoom.roomId, currentRoom.roomName)
            .show(childFragmentManager, "ROOM_OPTIONS_BOTTOM_SHEET")
    }

    override fun initSocketListeners() {
        homeFragmentVM.initializeSocketListeners(socketIOInstance)
    }

    private fun showRoomBottomSheet(buttonText: String, editTextHint: String, eventType: Int) {
        val bottomSheet = RoomUtilityBottomSheet.newInstance(buttonText, editTextHint, eventType)
        bottomSheet.setRoomOptionsCallback(this)
        bottomSheet.show(childFragmentManager, "ROOM_OPTIONS_BOTTOM_SHEET")
    }

    private fun checkIfCanJoinRoom(roomId: String) {
        binding.progressBar.visibility = View.VISIBLE
        homeFragmentVM.checkIfCanJoinRoom(firebaseInstance, roomId)
    }

    private fun joinChatRoom(roomId: String): String {
        binding.progressBar.visibility = View.VISIBLE
        return homeFragmentVM.joinRoom(socketIOInstance, roomId, firebaseInstance)
    }

    private fun createAndJoinRoom(roomName: String): String {
        binding.progressBar.visibility = View.VISIBLE
        return homeFragmentVM.createAndJoinRoom(socketIOInstance, firebaseInstance, roomName)
    }


    //starting chat activity
    private fun startChatActivity(roomId: String, roomName: String) {
        val intent = Intent(activity, ChatActivity::class.java).apply {
            putExtra("userName", firebaseInstance.currentUser?.displayName.toString())
            putExtra("roomID", roomId)
            putExtra("roomName", roomName)
        }
        Timber.tag("ChatActivity")
            .d("UserName: ${firebaseInstance.currentUser?.displayName}\nRoomId: $roomId\nRoomName: $roomName")
        Handler().postDelayed({
            binding.progressBar.visibility = View.GONE
            startActivity(intent)
        }, 3000)
    }

    //logging out
    private fun navigateToOnboardingFragment() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOnboardingFragment())
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun HomeScreenUI(
    ) {
        val showRooms = remember { mutableStateOf(false) }
        showRooms.value =
            !homeFragmentVM.responseAllUserActiveRoomsWithJoinerAvatar.observeAsState().value.isNullOrEmpty()
        val isLoading = homeFragmentVM.isLoadingRooms.observeAsState().value
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalDrawer(
                drawerShape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxSize(),
                drawerState = drawerState,
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        DrawerContent(onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                        })
                    }
                },
                content = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Surface(color = AppTheme.colors.appBackgroundLightGray) {
                            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                                val (appName, avatar, darkSurface, randomButton, centerNoRoomsDisplay, floatingActionButton, activeRoomsTv, lazyColumn, progressIndicator) = createRefs()
                                AppName(modifier = Modifier.constrainAs(appName) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                })
                                Avatar(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .padding(end = 10.dp, top = 10.dp)
                                        .constrainAs(avatar) {
                                            end.linkTo(parent.end)
                                            top.linkTo(parent.top)
                                        },
                                    avatarUrl = firebaseInstance.currentUser?.photoUrl.toString(),
                                    onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }
                                )
                                Surface(modifier = Modifier
                                    .constrainAs(darkSurface) {
                                        top.linkTo(appName.bottom)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                    }
                                    .padding(top = 5.dp)
                                    .fillMaxSize(),
                                    color = AppTheme.colors.appBackgroundDarkGray,
                                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                ) {
                                    FloatingActionMenu(
                                        modifier = Modifier
                                            .constrainAs(floatingActionButton) {
                                                end.linkTo(darkSurface.end, margin = 16.dp)
                                                bottom.linkTo(darkSurface.bottom, margin = 16.dp)
                                            },
                                        onCreateRoom = {
                                            showRoomBottomSheet(
                                                "Create room",
                                                "Room's nick name",
                                                1
                                            )
                                        },
                                        onJoinRoom = {
                                            showRoomBottomSheet("Join room", "Enter room id", 2)
                                        }
                                    )
                                }
                                RandomButton(modifier = Modifier
                                    .constrainAs(randomButton) {
                                        end.linkTo(darkSurface.end)
                                        top.linkTo(darkSurface.top)
                                    }
                                    .padding(top = 15.dp, end = 15.dp),
                                    onClick = { toast("Coming soon") })

                                if (isLoading == true) {
                                    Text(
                                        modifier = Modifier.constrainAs(activeRoomsTv) {
                                            top.linkTo(randomButton.bottom, 5.dp)
                                            start.linkTo(darkSurface.start, margin = 20.dp)
                                        },
                                        text = "Active Rooms",
                                        color = Color.White,
                                        style = TextStyle(
                                            fontFamily = FontFamily(Font(R.font.sans_med)),
                                            fontSize = 20.sp
                                        )
                                    )
                                    LazyColumn(modifier = Modifier.constrainAs(lazyColumn) {
                                        top.linkTo(activeRoomsTv.bottom, 5.dp)
                                        start.linkTo(parent.start, 25.dp)
                                        end.linkTo(parent.end, 25.dp)
                                    }) {
                                        items(count = 3) { index ->
                                            ShimmerItem()
                                        }
                                    }
                                } else {
                                    if (!showRooms.value) {
                                        MiddleNoActiveRooms(
                                            modifier = Modifier.constrainAs(
                                                centerNoRoomsDisplay
                                            ) {
                                                centerTo(darkSurface)
                                            })
                                    } else {
                                        Text(
                                            modifier = Modifier.constrainAs(activeRoomsTv) {
                                                top.linkTo(randomButton.bottom, 5.dp)
                                                start.linkTo(darkSurface.start, margin = 20.dp)
                                            },
                                            text = "Active Rooms",
                                            color = Color.White,
                                            style = TextStyle(
                                                fontFamily = FontFamily(Font(R.font.sans_med)),
                                                fontSize = 20.sp
                                            )
                                        )
                                        SetupLazyColumn(
                                            homeFragmentVM.responseAllUserActiveRoomsWithJoinerAvatar.observeAsState().value!!,
                                            Modifier.constrainAs(lazyColumn) {
                                                top.linkTo(activeRoomsTv.bottom, 5.dp)
                                                start.linkTo(parent.start, 25.dp)
                                                end.linkTo(parent.end, 25.dp)
                                            },
                                            firebaseInstance
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun SetupLazyColumn(
        mapOfActiveRoomsWithCreatorAndJoinerId:
        MutableMap<ActiveRooms, Pair<String, String?>>,
        modifier: Modifier,
        firebaseInstance: FirebaseAuth?,
    ) {
        LazyColumn(modifier = modifier) {
            itemsIndexed(mapOfActiveRoomsWithCreatorAndJoinerId.toList()) { index, (activeRooms, pairOfCreatorAndJoiner) ->
                HomeScreenRoomItem(
                    currentActiveRoom = activeRooms,
                    firebaseAuth = firebaseInstance!!,
                    creatorAvatarUrl = pairOfCreatorAndJoiner?.first,
                    roomJoinerAvatarUrl = pairOfCreatorAndJoiner?.second,
                    onClickListener = { clickedRoomId ->
                        roomId = clickedRoomId
                        checkIfCanJoinRoom(clickedRoomId)
                    },
                    onLongPressListener = { clickedActiveRoom ->
                        showRoomOptionsBottomSheet(clickedActiveRoom)
                    }
                )
            }
        }
    }

    @Composable
    fun DrawerContent(modifier: Modifier = Modifier, onClick: () -> Unit) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(1.dp)
                .verticalScroll(rememberScrollState())
                .clipToBounds(),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Quick Access",
                    style = TextStyle(fontSize = 12.sp),
                    fontFamily = FontFamily(Font(R.font.sharp_grotesk_medium20)),
                    color = Color(0xFF7E7E7E)
                )
                Column(modifier = Modifier) {
                    DrawerItem(
                        icon = painterResource(id = R.drawable.ic_logout),
                        text = "Sign out",
                        onClick = {
                            onClick.invoke()
                            signOutUser()
                        }
                    )
                }
            }
        }
    }

    private fun signOutUser() {
        homeFragmentVM.signOutUser()
    }

    override fun onRoomCreateAndJoinCallback(roomName: String) {
        roomId = createAndJoinRoom(roomName)
    }

    override fun onRoomJoinCallback(roomIdSent: String) {
        roomId = roomIdSent
        roomId?.let {
            checkIfCanJoinRoom(it)
        }
    }
}