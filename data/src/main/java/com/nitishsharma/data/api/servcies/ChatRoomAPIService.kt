package com.nitishsharma.data.api.servcies

import com.nitishsharma.domain.api.models.canjoinroom.CanJoinRoom
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface ChatRoomAPIService {
    @POST("/db/getAllUserActiveRooms")
    suspend fun getAllActiveRooms(@Body allUserActiveRoomsBody: AllUserActiveRoomsBody): AllUserActiveRooms

    @POST("/db/deleteCurrentRoom")
    suspend fun deleteCurrentRoom(@Body deleteRoomBody: JSONObject): Response<Unit>

    @POST("/db/saveUserToDb")
    suspend fun saveUserToDb(@Body saveUserToDbBody: JSONObject): Response<Unit>

    @POST("/db/canJoinRoom")
    suspend fun canJoinRoom(@Body canJoinRoomBody: JSONObject): Response<CanJoinRoom>

    @POST("/db/updateRoomIsAvailableStatus")
    suspend fun updateRoomIsAvailableStatus(@Body roomAvailableStatusBody: JSONObject): Response<Unit>

    @POST("db/updateJoinerInChatRoom")
    suspend fun updateRoomJoinerIdUseCase(@Body roomJoinerIdBody: JSONObject): Response<Unit>
}