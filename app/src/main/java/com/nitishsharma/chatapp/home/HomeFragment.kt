package com.nitishsharma.chatapp.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
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
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.Socket


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeFragmentArgs: HomeFragmentArgs by navArgs()
    private val firebaseInstance = FirebaseAuth.getInstance()
    private val homeFragmentVM: HomeFragmentViewModel by activityViewModels()
    private lateinit var bottomSheetDialog: BottomSheetDialog
    var roomId: String? = null
    var socketIOInstance: Socket? = null
    lateinit var drawerLayout: DrawerLayout
    private var adapter: RecyclerView.Adapter<ActiveRoomsAdapter.ViewHolder>? = null //adapter
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        socketIOInstance = (activity as MainActivity).socketIOInstance
        drawerLayout = binding.drawerLayout
        shimmerFrameLayout = binding.shimmerFrameLayout

        //initializing the views
        getAllUserActiveRooms()
        initViews()
        initializeSocketListeners()
        initializeObservers()

        binding.apply {
            createRoomButton.setOnClickListener {
                showBottomSheet("Create room", "Room's nick name", 1)
            }
            joinRoomButton.setOnClickListener {
                showBottomSheet("Join room", "Enter room id", 2)
            }
            moreOptions.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.END)
            }
            swipeRefresh.setOnRefreshListener {
                getAllUserActiveRooms()
            }
        }
        return binding.root
    }

    private fun initializeObservers() {
        homeFragmentVM.receivedRoomName.observe(requireActivity(), Observer { receivedName ->
            Log.i("HomeFrag", receivedName.toString())
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
        adapter = ActiveRoomsAdapter(activeRooms = activeRooms)
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
        binding.apply {
            recyclerView.visibility = View.VISIBLE
            activeRoomsTv.visibility = View.VISIBLE
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(activity)
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

    private fun showBottomSheet(buttonText: String, editTextHint: String, eventType: Int) {
        val view = layoutInflater.inflate(R.layout.join_room_bottom_sheet, null)
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
        return homeFragmentVM.joinRoom(socketIOInstance, roomId, firebaseInstance)
    }

    private fun createAndJoinRoom(roomName: String): String {
        return homeFragmentVM.createAndJoinRoom(socketIOInstance, firebaseInstance, roomName)
    }


    //starting chat activity
    private fun startChatActivity(roomId: String, roomName: String) {
        val intent = Intent(activity, ChatActivity::class.java)
        intent.putExtra("userName", firebaseInstance.currentUser?.displayName.toString())
        intent.putExtra("roomID", roomId)
        intent.putExtra("roomName", roomName)
        Log.i("ChatAct1", "${firebaseInstance.currentUser?.displayName}, $roomId, $roomName")
        Handler().postDelayed({
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