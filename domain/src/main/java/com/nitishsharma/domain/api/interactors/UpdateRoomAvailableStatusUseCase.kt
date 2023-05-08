package com.nitishsharma.domain.api.interactors

import android.os.Bundle
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import org.koin.core.component.KoinComponent

class UpdateRoomAvailableStatusUseCase constructor(private val chatRoomAPIRepository: ChatRoomAPIRepository) :
    KoinComponent {
    suspend operator fun invoke(isAvailable: Boolean, roomId: String) =
        chatRoomAPIRepository.updateRoomIsAvailableStatus(
            com.nitishsharma.domain.api.utility.Utils.bundleToJSONMapping(
                null,
                Bundle().apply {
                    putBoolean("isAvailable", isAvailable)
                    putString("roomId", roomId)
                })
        )
}