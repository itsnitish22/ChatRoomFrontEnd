package com.nitishsharma.domain.api.models.roomsresponse

import com.google.gson.annotations.SerializedName

data class AllUserActiveRooms(
    @SerializedName("count")
    val numberOfActiveRooms: Int,
    @SerializedName("active_rooms")
    val activeRooms: ArrayList<ActiveRooms>
)

data class ActiveRooms(
    @SerializedName("room_id")
    val roomId: String,
    @SerializedName("creator_id")
    val creatorId: String,
    @SerializedName("joiner_id")
    val joinerId: String?,
    @SerializedName("room_name")
    val roomName: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_available")
    val isAvailable: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("last_activity")
    val lastActivity: String
)

data class AllUserActiveRoomsBody(
    val userId: String
)

object ConvertToBodyForAllUserActiveRooms {
    fun convert(userId: String): AllUserActiveRoomsBody {
        return AllUserActiveRoomsBody(userId)
    }
}
