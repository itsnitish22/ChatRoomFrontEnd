package com.nitishsharma.chatapp.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.models.roomsresponse.ActiveRooms
import timber.log.Timber

class ActiveRoomsAdapter(
    private val activeRooms: ArrayList<ActiveRooms>,
    private val optionsItemClick: OptionsItemClickListener,
    private val viewItemClick: ViewItemClickListener
) : RecyclerView.Adapter<ActiveRoomsAdapter.ViewHolder>() {

    interface OptionsItemClickListener {
        fun onOptionsItemClick(position: Int, currentRoom: ActiveRooms)
    }

    interface ViewItemClickListener {
        fun onViewItemClick(position: Int, currentRoom: ActiveRooms)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActiveRoomsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_rooms, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Timber.tag("ActiveRoomsAdapter").d("Active Rooms: $activeRooms")
        return activeRooms.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentActiveRoom = activeRooms[position]
        holder.roomName.text = currentActiveRoom.roomName
        holder.roomId.text = currentActiveRoom.roomId

        holder.optionsMenu.setOnClickListener {
            optionsItemClick.onOptionsItemClick(position, currentActiveRoom)
        }
        holder.itemView.setOnClickListener {
            viewItemClick.onViewItemClick(position, currentActiveRoom)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomName: TextView = itemView.findViewById(R.id.roomNameTv)
        val roomId: TextView = itemView.findViewById(R.id.roomIdTv)
        val optionsMenu: ImageView = itemView.findViewById(R.id.optionsMenu)
    }
}