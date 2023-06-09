package com.nitishsharma.domain.api.interactors

import android.os.Bundle
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import com.nitishsharma.domain.api.utility.Utils
import org.koin.core.component.KoinComponent

class GetRoomDetailsUseCase constructor(private val chatRoomAPIRepository: ChatRoomAPIRepository) :
    KoinComponent {
    suspend operator fun invoke(roomId: String) = chatRoomAPIRepository.getRoomDetailsFromRoomId(
        Utils.bundleToJSONMapping(
            null,
            Bundle().apply {
                putString("roomId", roomId)
            })
    )
}