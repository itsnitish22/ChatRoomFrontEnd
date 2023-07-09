package com.nitishsharma.data.api.servcies

import com.nitishsharma.domain.api.interactors.Resource
import com.nitishsharma.domain.api.models.canjoinroom.CanJoinRoom
import com.nitishsharma.domain.api.models.deleteroom.DeleteRoom
import com.nitishsharma.domain.api.models.otheroomsarray.GetDistinctRoomIdsFromArray
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRooms
import com.nitishsharma.domain.api.models.roomsresponse.AllUserActiveRoomsBody
import com.nitishsharma.domain.api.models.useravatar.GetUserAvatar
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import java.io.IOException

class ChatRoomAPIRepositoryImpl : ChatRoomAPIRepository, KoinComponent {
    private val chatRoomAPIService: ChatRoomAPIService by inject()

    inline fun <reified R> toFlow(
        emitSuccessOnEmptyResponse: Boolean = false,
        noinline block: suspend () -> Response<R>
    ): Flow<Resource<R>> = flow {
        try {
            emit(Resource.Loading<R>())
            val data: Response<R> = block()
            if (data.isSuccessful) {
                data.body()?.let {
                    emit(Resource.Success<R>(it))
                } ?: if (emitSuccessOnEmptyResponse) {
                    emit(
                        Resource.Success(
                            when (R::class.java) {
                                Unit::class.java -> Unit
                                else -> error(error("Unsupported return type ${R::class.java}. Should use Response<Unit>"))
                            } as R
                        )
                    )
                }
            } else
                emit(Resource.Error<R>(IOException(data.message())))
        } catch (e: Exception) {
            emit(Resource.Error<R>(e))
        }
    }

    override suspend fun saveUserToDb(saveUserToDbBody: JSONObject): Response<Unit> {
        return chatRoomAPIService.saveUserToDb(saveUserToDbBody)
    }

    override suspend fun canJoinRoom(canJoinRoomBody: JSONObject): Response<CanJoinRoom> {
        return chatRoomAPIService.canJoinRoom(canJoinRoomBody)
    }

    override suspend fun updateRoomIsAvailableStatus(roomAvailableStatusBody: JSONObject): Response<Unit> {
        return chatRoomAPIService.updateRoomIsAvailableStatus(roomAvailableStatusBody)
    }

    override suspend fun updateRoomJoinerId(roomJoinerIdBody: JSONObject): Response<Unit> {
        return chatRoomAPIService.updateRoomJoinerIdUseCase(roomJoinerIdBody)
    }

    override suspend fun getUserAvatar(userAvatarBody: JSONObject): Response<GetUserAvatar> {
        return chatRoomAPIService.getUserAvatar(userAvatarBody)
    }

    override suspend fun addRoomToOtherRoomsArray(roomToOtherRoomsBody: JSONObject): Response<Unit> {
        return chatRoomAPIService.addRoomToOtherRoomsArray(roomToOtherRoomsBody)
    }

    override suspend fun getAllActiveRooms(allUserActiveRoomsBody: AllUserActiveRoomsBody): Response<AllUserActiveRooms> {
        return chatRoomAPIService.getAllActiveRooms(allUserActiveRoomsBody)
    }

    override suspend fun deleteCurrentRoom(deleteRoomBody: JSONObject): Response<DeleteRoom> {
        return chatRoomAPIService.deleteCurrentRoom(deleteRoomBody)
    }

    override suspend fun getAllDistinctRoomsFromArrayOfOtherRooms(gettingDistinctRoomsBody: JSONObject): Response<GetDistinctRoomIdsFromArray> {
        return chatRoomAPIService.getAllDistinctRoomsFromArrayOfOtherRooms(gettingDistinctRoomsBody)
    }

    override suspend fun getRoomDetailsFromRoomId(roomDetailsFromRoomId: JSONObject): Response<AllUserActiveRooms> {
        return chatRoomAPIService.getRoomDetailsFromRoomId(roomDetailsFromRoomId)
    }
}
