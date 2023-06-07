package com.nitishsharma.domain.api.repository

import com.nitishsharma.domain.api.models.canjoinroom.CanJoinRoom
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import com.nitishsharma.domain.api.models.useravatar.GetUserAvatar
import org.json.JSONObject
import retrofit2.Response

interface ChatRoomAPIRepository {
    suspend fun getAllActiveRooms(allUserActiveRoomsBody: AllUserActiveRoomsBody): Response<AllUserActiveRooms>
    suspend fun deleteCurrentRoom(deleteRoomBody: JSONObject): Response<Unit>
    suspend fun saveUserToDb(saveUserToDbBody: JSONObject): Response<Unit>
    suspend fun canJoinRoom(canJoinRoomBody: JSONObject): Response<CanJoinRoom>
    suspend fun updateRoomIsAvailableStatus(roomAvailableStatusBody: JSONObject): Response<Unit>
    suspend fun updateRoomJoinerId(roomJoinerIdBody: JSONObject): Response<Unit>
    suspend fun getUserAvatar(userAvatarBody: JSONObject): Response<GetUserAvatar>
}