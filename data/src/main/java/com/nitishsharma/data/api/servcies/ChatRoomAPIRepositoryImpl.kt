package com.nitishsharma.data.api.servcies

import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatRoomAPIRepositoryImpl : ChatRoomAPIRepository, KoinComponent {
    private val chatRoomAPIService: ChatRoomAPIService by inject()

    override suspend fun saveUserToDb(saveUserToDbBody: JSONObject) {
        chatRoomAPIService.saveUserToDb(saveUserToDbBody)
    }

    override suspend fun getAllActiveRooms(allUserActiveRoomsBody: AllUserActiveRoomsBody): AllUserActiveRooms {
        return chatRoomAPIService.getAllActiveRooms(allUserActiveRoomsBody)
    }

    override suspend fun deleteCurrentRoom(deleteRoomBody: JSONObject) {
        chatRoomAPIService.deleteCurrentRoom(deleteRoomBody)
    }
}