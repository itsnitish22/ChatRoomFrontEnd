package com.nitishsharma.chatapp.api.retrofit

import com.nitishsharma.chatapp.models.allfreeusers.GetAllFreeUsers
import com.nitishsharma.chatapp.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.chatapp.models.roomsresponse.AllUserActiveRoomsBody
import org.json.JSONObject
import retrofit2.http.*

interface ChatRoomAPI {
    @POST("/db/getAllUserActiveRooms")
    suspend fun getAllActiveRooms(@Body allUserActiveRoomsBody: AllUserActiveRoomsBody): AllUserActiveRooms

    @POST("/db/deleteCurrentRoom")
    suspend fun deleteCurrentRoom(@Body deleteRoomBody: JSONObject): Unit

    @POST("/db/saveUserToDb")
    suspend fun saveUserToDb(@Body saveUserToDbBody: JSONObject): Unit

    @POST("/db/setUserIsFreeStatus")
    suspend fun setUserIsFreeStatus(@Body setUserIsFreeStatusBody: JSONObject): Unit

    @POST("/db/getAllFreeUsers")
    suspend fun getAllFreeUsers(): GetAllFreeUsers
}