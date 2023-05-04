package com.nitishsharma.data.api.servcies

import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import org.json.JSONObject
import retrofit2.http.*

interface ChatRoomAPIService {
    @POST("/db/getAllUserActiveRooms")
    suspend fun getAllActiveRooms(@Body allUserActiveRoomsBody: AllUserActiveRoomsBody): AllUserActiveRooms

    @POST("/db/deleteCurrentRoom")
    suspend fun deleteCurrentRoom(@Body deleteRoomBody: JSONObject): Unit

    @POST("/db/saveUserToDb")
    suspend fun saveUserToDb(@Body saveUserToDbBody: JSONObject): Unit
}