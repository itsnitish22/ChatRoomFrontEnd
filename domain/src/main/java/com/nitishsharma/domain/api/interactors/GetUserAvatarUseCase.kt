package com.nitishsharma.domain.api.interactors

import android.os.Bundle
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import com.nitishsharma.domain.api.utility.Utils
import org.koin.core.component.KoinComponent

class GetUserAvatarUseCase constructor(private val chatRoomAPIRepository: ChatRoomAPIRepository) :
    KoinComponent {
    suspend operator fun invoke(userId: String) =
        chatRoomAPIRepository.getUserAvatar(Utils.bundleToJSONMapping(null, Bundle().apply {
            putString("userId", userId)
        }))
}