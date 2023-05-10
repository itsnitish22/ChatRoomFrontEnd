package com.nitishsharma.chatapp.main.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.nitishsharma.chatapp.MainActivity
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.chats.ChatActivity
import com.nitishsharma.chatapp.databinding.FragmentHomeBinding
import com.nitishsharma.chatapp.databinding.RoomOptionsBottomSheetBinding
import com.nitishsharma.chatapp.utils.Utility.copyTextToClipboard
import com.nitishsharma.chatapp.utils.Utility.shareRoom
import com.nitishsharma.chatapp.utils.Utility.toast
import com.nitishsharma.domain.api.models.roomsresponse.ActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.ConvertToBodyForAllUserActiveRooms
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.Socket
import timber.log.Timber

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeFragmentArgs: HomeFragmentArgs by navArgs()
    private val firebaseInstance = FirebaseAuth.getInstance()
    private val homeFragmentVM: HomeFragmentViewModel by activityViewModels()
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var roomId: String? = null
    private var socketIOInstance: Socket? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentHomeBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        socketIOInstance = (activity as MainActivity).socketIOInstance
        drawerLayout = binding.drawerLayout
        shimmerFrameLayout = binding.shimmerFrameLayout

        //initializations
        initViews()
        initSocketListeners()
        initObservers()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.apply {
            createRoomButton.setOnClickListener {
                showRoomBottomSheet("Create room", "Room's nick name", 1)
            }
            joinRoomButton.setOnClickListener {
                showRoomBottomSheet("Join room", "Enter room id", 2)
            }
            moreOptions.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllUserActiveRooms()
    }

    private fun initObservers() {
        homeFragmentVM.receivedRoomName.observe(requireActivity(), Observer { receivedName ->
            roomId?.let {
                startChatActivity(it, receivedName.toString())
            }
        })

        homeFragmentVM.successSignOut.observe(requireActivity(), Observer { signOut ->
            if (signOut) {
                navigateToOnboardingFragment()
            }
        })

        homeFragmentVM.responseAllUserActiveRooms.observe(
            requireActivity(),
            Observer { allUserActiveRooms ->
                if (allUserActiveRooms.numberOfActiveRooms >= 1) {
                    setupViewForActiveRooms()
                    loadDataInLazyColum(allUserActiveRooms.activeRooms)
                } else {
                    setupViewForNoActiveRooms()
                }
            })

        homeFragmentVM.deleteRoomSuccess.observe(requireActivity(), Observer { deleteRoomSuccess ->
            if (deleteRoomSuccess) {
                getAllUserActiveRooms()
            }
        })

        homeFragmentVM.canJoinRoom.observe(requireActivity(), Observer { canJoinRoom ->
            if (canJoinRoom.canJoin) {
                roomId?.let {
                    updateRoomIsAvailableStatus(false, it)
                }
            } else {
                binding.progressBar.visibility = View.GONE
                toast(canJoinRoom.actionForUser)
            }
        })

        homeFragmentVM.changedRoomAvailableStatus.observe(
            requireActivity(),
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
            requireActivity(),
            Observer { updatedRoomJoinerId ->
                updatedRoomJoinerId?.let {
                    if (it) {
                        roomId?.let {
                            joinChatRoom(it)
                        }
                    }
                }
            })
    }

    private fun updateRoomJoinerId(uid: String?, roomId: String) {
        homeFragmentVM.updateRoomJoinerId(uid, roomId)
    }

    private fun updateRoomIsAvailableStatus(isRoomAvailable: Boolean, roomId: String) {
        homeFragmentVM.updateRoomIsAvailableStatus(isRoomAvailable, roomId)
    }

    private fun setupViewForActiveRooms() {
        binding.apply {
            activeRoomsTv.visibility = View.VISIBLE
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            composeViewLazyColumn.visibility = View.VISIBLE
            noActiveRoomsTv.visibility = View.GONE
            noActiveRoomsDescTv.visibility = View.GONE
            roomIv.visibility = View.GONE
        }
    }

    private fun setupViewForNoActiveRooms() {
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
        binding.apply {
            activeRoomsTv.visibility = View.GONE
            composeViewLazyColumn.visibility = View.GONE
            noActiveRoomsTv.visibility = View.VISIBLE
            noActiveRoomsDescTv.visibility = View.VISIBLE
            roomIv.visibility = View.VISIBLE
        }
    }


    private fun loadDataInLazyColum(activeRooms: ArrayList<ActiveRooms>) {
        binding.apply {
            composeViewLazyColumn.setContent {
                SetupLazyColumn(activeRooms = activeRooms)
            }
        }
    }

    private fun showRoomOptionsBottomSheet(currentRoom: ActiveRooms) {
        val ui: RoomOptionsBottomSheetBinding
        val view = RoomOptionsBottomSheetBinding.inflate(layoutInflater).also {
            ui = it
        }.root

        ui.apply {
            roomNameTv.text = currentRoom.roomName
            copyRoomId.setOnClickListener {
                copyTextToClipboard(currentRoom.roomId, "Room ID")
                toast("Copied")
                bottomSheetDialog.dismiss()
            }
            inviteSomeone.setOnClickListener {
                shareRoom(currentRoom.roomId, currentRoom.roomName)
                bottomSheetDialog.dismiss()
            }
            deleteCurrentRoom.setOnClickListener {
                homeFragmentVM.deleteCurrentRoom(currentRoom.roomId)
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.apply {
            setCancelable(true)
            setContentView(view)
            show()
        }
    }

    private fun initSocketListeners() {
        homeFragmentVM.initializeSocketListeners(socketIOInstance)
    }

    private fun showRoomBottomSheet(buttonText: String, editTextHint: String, eventType: Int) {
        val view = layoutInflater.inflate(R.layout.room_bottom_sheet, null)
        bottomSheetDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val button = view.findViewById<Button>(R.id.joinRoomButton)
        val enterEditText = view.findViewById<EditText>(R.id.enterRoomEditText)
        val editText = view.findViewById<TextInputLayout>(R.id.enterRoom)

        button.text = buttonText
        editText.hint = editTextHint


        if (eventType == 1) {
            enterEditText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            button.setOnClickListener {
                if (enterEditText.text.toString().isNotEmpty()) {
                    bottomSheetDialog.dismiss()
                    roomId = createAndJoinRoom(enterEditText.text.toString())
                }
            }
        } else {
            button.setOnClickListener {
                if (enterEditText.text.toString().isNotEmpty()) {
                    roomId = enterEditText.text.toString()
                    bottomSheetDialog.dismiss()
                    roomId?.let {
                        checkIfCanJoinRoom(it)
                    }
                }
            }

        }

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
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
        val intent = Intent(activity, ChatActivity::class.java)
        intent.putExtra("userName", firebaseInstance.currentUser?.displayName.toString())
        intent.putExtra("roomID", roomId)
        intent.putExtra("roomName", roomName)
        Timber.tag("ChatActivity")
            .d("UserName: ${firebaseInstance.currentUser?.displayName}\nRoomId: $roomId\nRoomName: $roomName")
        Handler().postDelayed({
            binding.progressBar.visibility = View.GONE
            startActivity(intent)
        }, 3000)
    }

    //logging out
    private fun navigateToOnboardingFragment() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        }
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOnboardingFragment())
    }

    //initializing the views
    private fun initViews() {
        homeFragmentArgs.firebaseUser?.let {
            loadImageFromUrl(binding.profilePic, it.photoUrl)
            binding.nameOfUser.text = firebaseInstance.currentUser?.displayName
        }
    }

    //loading image from url
    private fun loadImageFromUrl(profilePic: CircleImageView, photoUrl: Uri?) {
        val options: RequestOptions = RequestOptions()
            .centerCrop()

        Glide.with(this).load(photoUrl).apply(options).into(profilePic)
    }

    private fun getAllUserActiveRooms() {
        shimmerFrameLayout.startShimmer()
        homeFragmentVM.getAllUserActiveRooms(
            ConvertToBodyForAllUserActiveRooms.convert(
                firebaseInstance.currentUser!!.uid
            )
        )
    }

    @Composable
    fun SetupLazyColumn(activeRooms: ArrayList<ActiveRooms>) {
        var refreshing by remember { mutableStateOf(false) }
        LaunchedEffect(refreshing) {
            if (refreshing) {
                getAllUserActiveRooms()
                refreshing = false
            }
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = refreshing),
            onRefresh = { refreshing = true }
        ) {
            LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                items(items = activeRooms) { currentActiveRoom ->
                    ActiveRoomsItem(currentActiveRoom = currentActiveRoom)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ActiveRoomsItem(currentActiveRoom: ActiveRooms) {
        Surface(
            color = colorResource(R.color.light_blue),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            shape = RoundedCornerShape(10.dp),
            onClick = {
                roomId = currentActiveRoom.roomId
                joinChatRoom(currentActiveRoom.roomId)
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentActiveRoom.roomName,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.DarkGray
                        )
                        Text(
                            text = currentActiveRoom.roomId,
                            color = Color.DarkGray,
                            fontSize = 12.sp
                        )
                    }
                    Box {
                        IconButton(
                            onClick = {
                                showRoomOptionsBottomSheet(currentActiveRoom)
                            },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_dots_vertical),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }
}

//join chat room
//first check if joiner_id in chatting_rooms where id = current_room_id is null
//if null, join room
//else toast, room is full