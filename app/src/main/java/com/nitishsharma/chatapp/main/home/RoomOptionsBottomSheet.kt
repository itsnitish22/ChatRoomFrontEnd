package com.nitishsharma.chatapp.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nitishsharma.chatapp.databinding.RoomOptionsBottomSheetBinding
import com.nitishsharma.chatapp.utils.Utility.copyTextToClipboard
import com.nitishsharma.chatapp.utils.Utility.shareRoom
import com.nitishsharma.chatapp.utils.Utility.toast

class RoomOptionsBottomSheet : BottomSheetDialogFragment() {
    private val bottomSheetVM: HomeFragmentViewModel by activityViewModels()
    private lateinit var binding: RoomOptionsBottomSheetBinding
    private lateinit var roomId: String
    private lateinit var roomName: String

    companion object {
        fun newInstance(
            roomId: String,
            roomName: String
        ): RoomOptionsBottomSheet {
            val fragment = RoomOptionsBottomSheet()
            val args = Bundle().apply {
                putString("ROOM_ID", roomId)
                putString("ROOM_NAME", roomName)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = RoomOptionsBottomSheetBinding.inflate(inflater, container, false).also {
        binding = it
        roomId =
            arguments?.getString("ROOM_ID")!!
        roomName =
            arguments?.getString("ROOM_NAME")!!
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initClickListeners()
    }

    private fun initViews() {
        binding.roomNameTv.text = roomName
    }

    private fun initClickListeners() {
        binding.apply {
            copyRoomId.setOnClickListener {
                copyTextToClipboard(roomId, "Room ID")
                toast("Copied")
                dismiss()
            }
            inviteSomeone.setOnClickListener {
                shareRoom(roomId, roomName)
                dismiss()
            }
            deleteCurrentRoom.setOnClickListener {
                bottomSheetVM.deleteCurrentRoom(roomId)
                dismiss()
            }
        }
    }
}