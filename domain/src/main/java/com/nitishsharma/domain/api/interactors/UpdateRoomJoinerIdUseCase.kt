package com.nitishsharma.domain.api.interactors

import android.os.Bundle
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import com.nitishsharma.domain.api.utility.Utils
import org.koin.core.component.KoinComponent

class UpdateRoomJoinerIdUseCase constructor(private val chatRoomAPIRepository: ChatRoomAPIRepository) :
    KoinComponent {
    suspend operator fun invoke(userId: String?, roomId: String) =
        chatRoomAPIRepository.updateRoomJoinerId(Utils.bundleToJSONMapping(null, Bundle().apply {
            putString("userId", userId)
            putString("roomId", roomId)
        }))
}