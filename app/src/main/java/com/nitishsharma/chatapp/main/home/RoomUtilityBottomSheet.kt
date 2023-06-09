package com.nitishsharma.chatapp.main.home

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.databinding.RoomBottomSheetBinding

class RoomUtilityBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: RoomBottomSheetBinding
    private lateinit var buttonText: String
    private lateinit var editTextHint: String
    private var eventType: Int = -1

    private var callback: RoomUtilityCallback? = null
    fun setRoomOptionsCallback(callback: RoomUtilityCallback) {
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    interface RoomUtilityCallback {
        fun onRoomCreateAndJoinCallback(roomName: String)
        fun onRoomJoinCallback(roomIdSent: String)
    }

    companion object {
        fun newInstance(
            buttonText: String, editTextHint: String, eventType: Int
        ): RoomUtilityBottomSheet {
            val fragment = RoomUtilityBottomSheet()
            val args = Bundle().apply {
                putString("BUTTON_TEXT", buttonText)
                putString("EDIT_TEXT_HINT", editTextHint)
                putInt("EVENT_TYPE", eventType)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = RoomBottomSheetBinding.inflate(inflater, container, false).also {
        binding = it
        buttonText =
            arguments?.getString("BUTTON_TEXT")!!
        editTextHint =
            arguments?.getString("EDIT_TEXT_HINT")!!
        eventType =
            arguments?.getInt("EVENT_TYPE")!!
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.apply {
            if (eventType == 1) {
                enterRoomEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                joinRoomButton.setOnClickListener {
                    if (enterRoomEditText.text.toString().isNotEmpty()) {
                        callback?.onRoomCreateAndJoinCallback(enterRoomEditText.text.toString())
                        dismiss()
                    }
                }
            } else {
                joinRoomButton.setOnClickListener {
                    if (enterRoomEditText.text.toString().isNotEmpty()) {
                        callback?.onRoomJoinCallback(enterRoomEditText.text.toString())
                        dismiss()
                    }
                }
            }
        }
    }

    private fun initViews() {
        binding.joinRoomButton.text = buttonText
        binding.enterRoom.hint = editTextHint
    }
}