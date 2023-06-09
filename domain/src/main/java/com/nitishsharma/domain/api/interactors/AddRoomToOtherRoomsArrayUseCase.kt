package com.nitishsharma.domain.api.interactors

import android.os.Bundle
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import com.nitishsharma.domain.api.utility.Utils
import org.koin.core.component.KoinComponent

class AddRoomToOtherRoomsArrayUseCase constructor(private val apiRepository: ChatRoomAPIRepository) :
    KoinComponent {
    suspend operator fun invoke(roomId: String, userId: String?) =
        apiRepository.addRoomToOtherRoomsArray(
            Utils.bundleToJSONMapping(null, Bundle().apply {
                putString("roomId", roomId)
                putString("userId", userId)
            })
        )
}