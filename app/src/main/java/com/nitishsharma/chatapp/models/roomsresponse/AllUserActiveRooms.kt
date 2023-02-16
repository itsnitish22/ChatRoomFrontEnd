package com.nitishsharma.chatapp.models.roomsresponse

import com.google.gson.annotations.SerializedName

data class AllUserActiveRooms(
    @SerializedName("active_rooms")
    val activeRooms: ArrayList<ActiveRooms>
)

data class ActiveRooms(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("room_id")
    val roomId: String,
    @SerializedName("room_name")
    val roomName: String,
    @SerializedName("is_active")
    val isActive: Boolean
)

data class AllUserActiveRoomsBody(
    val userId: String
)

object ConvertToBodyForAllUserActiveRooms {
    fun convert(userId: String): AllUserActiveRoomsBody {
        return AllUserActiveRoomsBody(userId)
    }
}
