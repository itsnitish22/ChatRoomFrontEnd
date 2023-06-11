package com.nitishsharma.chatapp.main.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitishsharma.chatapp.databinding.ItemReceivedMessageBinding
import com.nitishsharma.chatapp.databinding.ItemSentMessageBinding
import com.nitishsharma.chatapp.databinding.ItemTypingMessageBinding
import org.json.JSONObject

class MessageAdapter(private val inflater: LayoutInflater) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<JSONObject>()

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.getBoolean("isSent")) {
            if (message.has("message")) TYPE_MESSAGE_SENT else -1
        } else {
            if (message.has("message"))
                TYPE_MESSAGE_RECEIVED
            else if (message.has("typing"))
                TYPE_USER_TYPING
            else -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = when (viewType) {
            TYPE_MESSAGE_SENT -> ItemSentMessageBinding.inflate(inflater, parent, false)
            TYPE_MESSAGE_RECEIVED -> ItemReceivedMessageBinding.inflate(inflater, parent, false)
            TYPE_USER_TYPING -> ItemTypingMessageBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return when (viewType) {
            TYPE_MESSAGE_SENT -> SentMessageVH(binding as ItemSentMessageBinding)
            TYPE_MESSAGE_RECEIVED -> ReceivedMessageVH(binding as ItemReceivedMessageBinding)
            TYPE_USER_TYPING -> TypingMessageVH(binding as ItemTypingMessageBinding)
            else -> EmptyViewHolder(binding.root)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (message.has("message")) {
            when (holder) {
                is SentMessageVH -> holder.binding.sentTxt.text = message.getString("message")
                is ReceivedMessageVH -> {
                    holder.binding.nameTxt.text = message.getString("userName")
                    holder.binding.receivedTxt.text = message.getString("message")
                }
            }
        } else {
            when (holder) {
                is TypingMessageVH -> {
                    if (message.has("showTyping")) {
                        holder.itemView.visibility = View.VISIBLE
                        holder.binding.nameTxt.text = message.getString("userName")
                    }
                }
            }
        }
    }

    override fun getItemCount() = messages.size

    fun addItem(jsonObject: JSONObject) {
        messages.add(jsonObject)
        notifyItemInserted(messages.size - 1)
    }

    inner class SentMessageVH(val binding: ItemSentMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ReceivedMessageVH(val binding: ItemReceivedMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class TypingMessageVH(val binding: ItemTypingMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val TYPE_MESSAGE_SENT = 0
        private const val TYPE_MESSAGE_RECEIVED = 1
        private const val TYPE_USER_TYPING = 2
    }
}
