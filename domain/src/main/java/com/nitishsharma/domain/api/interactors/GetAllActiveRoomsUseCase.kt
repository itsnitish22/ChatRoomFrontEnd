package com.nitishsharma.domain.api.interactors

import android.os.Bundle
import com.nitishsharma.domain.api.models.roomsresponse.ActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import com.nitishsharma.domain.api.utility.Utils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetAllActiveRoomsUseCase constructor(private val chatRoomAPIRepository: ChatRoomAPIRepository) :
    KoinComponent {
    private val getUserAvatarUseCase: GetUserAvatarUseCase by inject()

    suspend operator fun invoke(allUserActiveRoomsBody: AllUserActiveRoomsBody): MutableMap<ActiveRooms, Pair<String, String?>> {
        val mapOfActiveRoomsWithCreatorAndJoiner: MutableMap<ActiveRooms, Pair<String, String?>> =
            mutableMapOf()

        val responseOfUserActiveRooms =
            chatRoomAPIRepository.getAllActiveRooms(allUserActiveRoomsBody)

        if (responseOfUserActiveRooms.isSuccessful) {
            val allActiveRooms = responseOfUserActiveRooms.body()?.activeRooms
            val responseOfOtherActiveRooms =
                chatRoomAPIRepository.getAllDistinctRoomsFromArrayOfOtherRooms(
                    Utils.bundleToJSONMapping(
                        null,
                        Bundle().apply {
                            putString("userId", allUserActiveRoomsBody.userId)
                        }
                    )
                )

            if (responseOfOtherActiveRooms.isSuccessful) {
                for (otherActiveRoomId in responseOfOtherActiveRooms.body()?.active_rooms!!) {
                    val roomDetails = chatRoomAPIRepository.getRoomDetailsFromRoomId(
                        Utils.bundleToJSONMapping(
                            null,
                            Bundle().apply {
                                putString("roomId", otherActiveRoomId)
                            }
                        )
                    )
                    if (roomDetails.isSuccessful && roomDetails.body()?.activeRooms?.isNotEmpty() == true) {
                        allActiveRooms?.add(roomDetails.body()?.activeRooms?.get(0)!!)
                    }
                }
            }

            if (allActiveRooms != null) {
                for (activeRooms in allActiveRooms) {
                    val joinerAvatarUrl = activeRooms.joinerId?.let {
                        getUserAvatarUseCase.invoke(it).body()?.userAvatar
                    }
                    val creatorAvatarUrl =
                        getUserAvatarUseCase.invoke(activeRooms.creatorId).body()?.userAvatar!!
                    val temp = Pair(creatorAvatarUrl, joinerAvatarUrl)
                    mapOfActiveRoomsWithCreatorAndJoiner[activeRooms] = temp
                }
            }
        }
        return mapOfActiveRoomsWithCreatorAndJoiner
    }
}
