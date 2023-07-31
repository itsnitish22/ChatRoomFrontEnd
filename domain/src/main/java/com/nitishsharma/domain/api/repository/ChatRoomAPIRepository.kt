package com.nitishsharma.domain.api.repository

import com.nitishsharma.domain.api.models.canjoinroom.CanJoinRoom
import com.nitishsharma.domain.api.models.deleteroom.DeleteRoom
import com.nitishsharma.domain.api.models.otheroomsarray.GetDistinctRoomIdsFromArray
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import com.nitishsharma.domain.api.models.useravatar.GetUserAvatar
import com.nitishsharma.domain.api.models.userexists.UserExists
import org.json.JSONObject
import retrofit2.Response

interface ChatRoomAPIRepository {
    suspend fun getAllActiveRooms(allUserActiveRoomsBody: AllUserActiveRoomsBody): Response<AllUserActiveRooms>
    suspend fun deleteCurrentRoom(deleteRoomBody: JSONObject): Response<DeleteRoom>
    suspend fun saveUserToDb(saveUserToDbBody: JSONObject): Response<Unit>
    suspend fun canJoinRoom(canJoinRoomBody: JSONObject): Response<CanJoinRoom>
    suspend fun updateRoomIsAvailableStatus(roomAvailableStatusBody: JSONObject): Response<Unit>
    suspend fun updateRoomJoinerId(roomJoinerIdBody: JSONObject): Response<Unit>
    suspend fun getUserAvatar(userAvatarBody: JSONObject): Response<GetUserAvatar>
    suspend fun addRoomToOtherRoomsArray(roomToOtherRoomsBody: JSONObject): Response<Unit>
    suspend fun getAllDistinctRoomsFromArrayOfOtherRooms(gettingDistinctRoomsBody: JSONObject): Response<GetDistinctRoomIdsFromArray>
    suspend fun getRoomDetailsFromRoomId(roomDetailsFromRoomId: JSONObject): Response<AllUserActiveRooms>
    suspend fun checkIfUserExists(userDetails: JSONObject): Response<UserExists>
}