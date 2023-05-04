package com.nitishsharma.domain.api.interactors

import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import org.json.JSONObject
import org.koin.core.component.KoinComponent

class DeleteCurrentRoomUseCase constructor(private val chatRoomAPIRepository: ChatRoomAPIRepository) :
    KoinComponent {
    suspend operator fun invoke(deleteRoomBody: JSONObject) =
        chatRoomAPIRepository.deleteCurrentRoom(deleteRoomBody)
}