package com.nitishsharma.domain.api.interactors

import android.os.Bundle
import com.nitishsharma.domain.api.models.canjoinroom.CanJoinRoom
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import com.nitishsharma.domain.api.utility.Utils
import org.koin.core.component.KoinComponent

class CanJoinRoomUseCase constructor(private val apiRepository: ChatRoomAPIRepository) :
    KoinComponent {
    suspend operator fun invoke(userId: String, roomId: String): CanJoinRoom? {
        val response = apiRepository.canJoinRoom(Utils.bundleToJSONMapping(null, Bundle().apply {
            putString("userId", userId);
            putString("roomId", roomId)
        }))

        return response.body()
    }
}