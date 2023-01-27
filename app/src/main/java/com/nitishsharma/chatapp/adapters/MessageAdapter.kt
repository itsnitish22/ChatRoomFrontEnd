package com.nitishsharma.chatapp.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitishsharma.chatapp.R
import org.json.JSONException
import org.json.JSONObject


class MessageAdapter(private val inflater: LayoutInflater) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messages: MutableList<JSONObject> = ArrayList()

    inner class SentMessageHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageTxt: TextView

        init {
            messageTxt = itemView.findViewById(R.id.sentTxt)
        }
    }

    inner class ReceivedMessageHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var nameTxt: TextView
        var messageTxt: TextView

        init {
            nameTxt = itemView.findViewById(R.id.nameTxt)
            messageTxt = itemView.findViewById(R.id.receivedTxt)
        }
    }

    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        try {
            return if (message.getBoolean("isSent")) {
                if (message.has("message"))
                    TYPE_MESSAGE_SENT else -1
            } else {
                if (message.has("message")) TYPE_MESSAGE_RECEIVED else -1
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            TYPE_MESSAGE_SENT -> {
                view = inflater.inflate(R.layout.item_sent_message, parent, false)
                return SentMessageHolder(view)
            }
            TYPE_MESSAGE_RECEIVED -> {
                view = inflater.inflate(R.layout.item_received_message, parent, false)
                return ReceivedMessageHolder(view)
            }
        }
        view = inflater.inflate(R.layout.item_sent_message, parent, false)
        return EmptyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        Log.d("MAdapter", message.toString())
        try {
            if (message.getBoolean("isSent")) {
                if (message.has("message")) {
                    val messageHolder = holder as SentMessageHolder
                    messageHolder.messageTxt.text = message.getString("message")
                }
            } else {
                if (message.has("message")) {
                    val messageHolder = holder as ReceivedMessageHolder
                    messageHolder.nameTxt.text = message.getString("name")
                    messageHolder.messageTxt.text = message.getString("message")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(jsonObject: JSONObject) {
        messages.add(jsonObject)
        Log.d("MAdapter", messages.size.toString())
        notifyDataSetChanged()
    }

    companion object {
        private const val TYPE_MESSAGE_SENT = 0
        private const val TYPE_MESSAGE_RECEIVED = 1
    }
}