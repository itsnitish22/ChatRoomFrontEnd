package com.nitishsharma.data.api.servcies

import com.nitishsharma.domain.api.models.canjoinroom.CanJoinRoom
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response

class ChatRoomAPIRepositoryImpl : ChatRoomAPIRepository, KoinComponent {
    private val chatRoomAPIService: ChatRoomAPIService by inject()

    override suspend fun saveUserToDb(saveUserToDbBody: JSONObject): Response<Unit> {
        return chatRoomAPIService.saveUserToDb(saveUserToDbBody)
    }

    override suspend fun canJoinRoom(canJoinRoomBody: JSONObject): Response<CanJoinRoom> {
        return chatRoomAPIService.canJoinRoom(canJoinRoomBody)
    }

    override suspend fun updateRoomIsAvailableStatus(roomAvailableStatusBody: JSONObject): Response<Unit> {
        return chatRoomAPIService.updateRoomIsAvailableStatus(roomAvailableStatusBody)
    }

    override suspend fun updateRoomJoinerId(roomJoinerIdBody: JSONObject): Response<Unit> {
        return chatRoomAPIService.updateRoomJoinerIdUseCase(roomJoinerIdBody)
    }

    override suspend fun getAllActiveRooms(allUserActiveRoomsBody: AllUserActiveRoomsBody): AllUserActiveRooms {
        return chatRoomAPIService.getAllActiveRooms(allUserActiveRoomsBody)
    }

    override suspend fun deleteCurrentRoom(deleteRoomBody: JSONObject) {
        chatRoomAPIService.deleteCurrentRoom(deleteRoomBody)
    }
}