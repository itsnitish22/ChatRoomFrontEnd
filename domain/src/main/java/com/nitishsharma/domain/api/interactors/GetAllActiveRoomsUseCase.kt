package com.nitishsharma.domain.api.interactors

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.nitishsharma.domain.api.models.roomsresponse.ActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import com.nitishsharma.domain.api.utility.Utils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetAllActiveRoomsUseCase constructor(private val chatRoomAPIRepository: ChatRoomAPIRepository) :
    KoinComponent {
    private val getUserAvatarUseCase: GetUserAvatarUseCase by inject()

    @SuppressLint("LongLogTag")
    suspend operator fun invoke(allUserActiveRoomsBody: AllUserActiveRoomsBody): MutableMap<ActiveRooms, Pair<String?, String?>?> {
        val mapOfActiveRoomsWithJoiners: MutableMap<ActiveRooms, String?> = mutableMapOf()
        val mapOfActiveRoomsWithCreatorAndJoiner: MutableMap<ActiveRooms, Pair<String?, String?>?> =
            mutableMapOf()

        val responseOfUserActiveRooms =
            chatRoomAPIRepository.getAllActiveRooms(allUserActiveRoomsBody)

        if (responseOfUserActiveRooms.isSuccessful) {
            Log.i(
                "HOLA User Active Initial",
                responseOfUserActiveRooms.body()?.activeRooms.toString()
            )
            val allActiveRooms =
                responseOfUserActiveRooms.body()?.activeRooms //initially user's own active rooms
            val responseOfOtherActiveRooms =
                chatRoomAPIRepository.getAllDistinctRoomsFromArrayOfOtherRooms( //getting user's other active rooms [user has joined someday]
                    com.nitishsharma.domain.api.utility.Utils.bundleToJSONMapping(
                        null,
                        Bundle().apply {
                            putString("userId", allUserActiveRoomsBody.userId)
                        })
                )
            Log.i(
                "HOLA All User Active Other",
                responseOfOtherActiveRooms.toString()
            )
            if (responseOfOtherActiveRooms.isSuccessful) {
                Log.i(
                    " HOLAInside If",
                    "HELLO IF"
                )
                for (otherActiveRoomId in responseOfOtherActiveRooms.body()?.active_rooms!!) {
                    Log.i(
                        "HOLA Inside For",
                        "Hello For"
                    )
                    val roomDetails = chatRoomAPIRepository.getRoomDetailsFromRoomId(
                        Utils.bundleToJSONMapping(
                            null,
                            Bundle().apply {
                                putString("roomId", otherActiveRoomId)
                            })
                    )
                    Log.i(
                        "HOLA Room Details Got",
                        roomDetails.toString()
                    )
                    if (roomDetails.isSuccessful) {
                        allActiveRooms?.let {
                            if (roomDetails.body()?.activeRooms?.size!! > 0) {
                                roomDetails.body()?.activeRooms?.get(0)
                                    ?.let { allActiveRooms.add(it) }
                                Log.i(
                                    "HOLA Adding To Array",
                                    "Added"
                                )
                            }
                        }
                    }
                }
            }
            Log.i("HOLA All Active Rooms Final", allActiveRooms.toString())
            if (responseOfUserActiveRooms.isSuccessful) {
                if (allActiveRooms != null) {
                    for (activeRooms in allActiveRooms) {
                        Log.i("HOLA Active Room", activeRooms.toString())
                        val joinerAvatarUrl =
                            if (activeRooms.joinerId != null) getUserAvatarUseCase.invoke(
                                activeRooms.joinerId
                            ).body()?.userAvatar else null
                        Log.i(
                            "HOLA Joiner Avatar",
                            joinerAvatarUrl.toString()
                        )
                        val creatorAvatarUrl = getUserAvatarUseCase.invoke(
                            activeRooms.creatorId
                        ).body()?.userAvatar
                        Log.i(
                            "HOLA Creator Avatar",
                            creatorAvatarUrl.toString()
                        )
                        val temp = Pair(creatorAvatarUrl, joinerAvatarUrl)
                        mapOfActiveRoomsWithCreatorAndJoiner[activeRooms] = temp
                        Log.i(
                            "HOLA Map Active Rooms",
                            "Inside Map"
                        )
                    }
                }
            }
        }
        Log.i(
            "HOLA Map Active Rooms Final",
            mapOfActiveRoomsWithCreatorAndJoiner.toString()
        )
        return mapOfActiveRoomsWithCreatorAndJoiner
    }
}