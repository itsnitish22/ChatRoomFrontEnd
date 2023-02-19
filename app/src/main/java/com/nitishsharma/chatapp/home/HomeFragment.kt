package com.nitishsharma.chatapp.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.nitishsharma.chatapp.MainActivity
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.chats.ChatActivity
import com.nitishsharma.chatapp.databinding.FragmentHomeBinding
import com.nitishsharma.chatapp.models.roomsresponse.ActiveRooms
import com.nitishsharma.chatapp.models.roomsresponse.ConvertToBodyForAllUserActiveRooms
import com.nitishsharma.chatapp.utils.Utility.copyTextToClipboard
import com.nitishsharma.chatapp.utils.Utility.shareRoom
import com.nitishsharma.chatapp.utils.Utility.toast
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
    private var adapter: RecyclerView.Adapter<ActiveRoomsAdapter.ViewHolder>? = null //adapter
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    @SuppressLint("UseRequireInsteadOfGet")
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

        //initializing the views
        initViews()
        initializeSocketListeners()
        initializeObservers()

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
            swipeRefresh.setOnRefreshListener {
                getAllUserActiveRooms()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllUserActiveRooms()
    }

    private fun initializeObservers() {
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
                if (binding.swipeRefresh.isRefreshing) {
                    binding.swipeRefresh.isRefreshing = false
                }
                if (allUserActiveRooms.numberOfActiveRooms >= 1)
                    setDataInRecyclerAdapterAndShowActiveRooms(allUserActiveRooms.activeRooms)
                else showNoActiveRooms()
            })
    }

    private fun setDataInRecyclerAdapterAndShowActiveRooms(activeRooms: ArrayList<ActiveRooms>) {
        adapter = ActiveRoomsAdapter(
            activeRooms = activeRooms,
            object : ActiveRoomsAdapter.OptionsItemClickListener {
                override fun onOptionsItemClick(position: Int, currentRoom: ActiveRooms) {
                    showRoomOptionsBottomSheet(currentRoom)
                }
            },
            object : ActiveRoomsAdapter.ViewItemClickListener {
                override fun onViewItemClick(position: Int, currentRoom: ActiveRooms) {
                    roomId = currentRoom.roomId
                    joinChatRoom(currentRoom.roomId)
                }

            }
        )
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
        binding.apply {
            recyclerView.visibility = View.VISIBLE
            activeRoomsTv.visibility = View.VISIBLE
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showRoomOptionsBottomSheet(currentRoom: ActiveRooms) {
        val view = layoutInflater.inflate(R.layout.room_options_bottom_sheet, null)

        val roomName = view.findViewById<AppCompatTextView>(R.id.roomNameTv)
        val inviteSomeone = view.findViewById<AppCompatTextView>(R.id.inviteSomeone)
        val copyRoomId = view.findViewById<AppCompatTextView>(R.id.copyRoomId)

        roomName.text = currentRoom.roomName
        copyRoomId.setOnClickListener {
            copyTextToClipboard(currentRoom.roomId, "Room ID")
            toast("Copied")
            bottomSheetDialog.dismiss()
        }
        inviteSomeone.setOnClickListener {
            shareRoom(currentRoom.roomId, currentRoom.roomName)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.apply {
            setCancelable(true)
            setContentView(view)
            show()
        }
    }

    private fun showNoActiveRooms() {
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
        binding.apply {
            noActiveRoomsTv.visibility = View.VISIBLE
            roomIv.visibility = View.VISIBLE
            noActiveRoomsDescTv.visibility = View.VISIBLE
        }
    }

    private fun initializeSocketListeners() {
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
                        joinChatRoom(it)
                    }
                }
            }

        }

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
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
}