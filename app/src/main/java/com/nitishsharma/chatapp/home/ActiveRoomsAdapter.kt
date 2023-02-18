package com.nitishsharma.chatapp.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.models.roomsresponse.ActiveRooms
import timber.log.Timber

class ActiveRoomsAdapter(private val activeRooms: ArrayList<ActiveRooms>) :
    RecyclerView.Adapter<ActiveRoomsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActiveRoomsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_rooms, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Timber.i("Active Rooms: $activeRooms")
        return activeRooms.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.roomNameTv)
        val roomId: TextView = itemView.findViewById(R.id.roomIdTv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentActiveRoom = activeRooms[position]
        holder.roomName.text = currentActiveRoom.roomName
        holder.roomId.text = currentActiveRoom.roomId
    }
}