package com.nitishsharma.chatapp.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.nitishsharma.chatapp.MainActivity
import com.nitishsharma.chatapp.chats.ChatActivity
import com.nitishsharma.chatapp.databinding.FragmentHomeBinding
import com.nitishsharma.chatapp.splashscreen.SplashFragmentDirections
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.Socket
import org.json.JSONObject
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeFragmentArgs: HomeFragmentArgs by navArgs()
    private val firebaseInstance = FirebaseAuth.getInstance()
    private val homeFragmentVM: HomeFragmentViewModel by viewModels()
    var socketIOInstance: Socket? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        socketIOInstance = (activity as MainActivity).socketIOInstance

        //initializing the views
        initViews()

        //on click profile pic
        binding.profilePic.setOnClickListener {
            logOutAccount()
        }

        binding.joinRoom.setOnClickListener {
            val roomID = createRoom(socketIOInstance)
            joinRoom(roomID)
            startChatActivity(roomID)
        }

        return binding.root
    }

    private fun joinRoom(roomID: String) {
        val jsonObject = JSONObject()
        jsonObject.put("roomid", roomID)
        jsonObject.put("roomid", firebaseInstance.currentUser?.displayName)
        socketIOInstance?.emit("join-room", jsonObject)
    }

    private fun createRoom(socketIOInstance: Socket?): String {
        val generatedRoomId = UUID.randomUUID().toString()
        socketIOInstance?.emit("create-room", generatedRoomId)
        return generatedRoomId
    }


    //starting chat activity
    private fun startChatActivity(roomId: String) {
        val intent = Intent(activity, ChatActivity::class.java)
        intent.putExtra("name", firebaseInstance.currentUser?.displayName.toString())
        intent.putExtra("roomID", roomId.toString())
        Log.i("ChatAct1", "${firebaseInstance.currentUser?.displayName}, $roomId")
        Handler().postDelayed({
            startActivity(intent)
        }, 3000)
    }

    //logging out
    private fun logOutAccount() {
        firebaseInstance.signOut()
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOnboardingFragment())
    }

    //initializing the views
    private fun initViews() {
        homeFragmentArgs.firebaseUser?.let {
            loadImageFromUrl(binding.profilePic, it.photoUrl)
        }
    }

    //loading image from url
    private fun loadImageFromUrl(profilePic: CircleImageView, photoUrl: Uri?) {
        val options: RequestOptions = RequestOptions()
            .centerCrop()

        Glide.with(this).load(photoUrl).apply(options).into(profilePic)
    }
}