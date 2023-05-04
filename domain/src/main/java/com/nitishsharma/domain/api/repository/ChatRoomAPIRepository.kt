package com.nitishsharma.domain.api.repository

import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import org.json.JSONObject

interface ChatRoomAPIRepository {
    suspend fun getAllActiveRooms(allUserActiveRoomsBody: AllUserActiveRoomsBody): AllUserActiveRooms
    suspend fun deleteCurrentRoom(deleteRoomBody: JSONObject): Unit
    suspend fun saveUserToDb(saveUserToDbBody: JSONObject): Unit
}