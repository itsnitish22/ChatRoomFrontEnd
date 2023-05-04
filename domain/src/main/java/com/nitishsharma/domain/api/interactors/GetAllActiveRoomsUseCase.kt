package com.nitishsharma.domain.api.interactors

import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import org.koin.core.component.KoinComponent

class GetAllActiveRoomsUseCase constructor(private val chatRoomAPIRepository: ChatRoomAPIRepository) :
    KoinComponent {
    suspend operator fun invoke(allUserActiveRoomsBody: AllUserActiveRoomsBody) =
        chatRoomAPIRepository.getAllActiveRooms(allUserActiveRoomsBody)
}