package com.nitishsharma.chatapp.di

import com.nitishsharma.domain.api.interactors.CanJoinRoomUseCase
import com.nitishsharma.domain.api.interactors.DeleteCurrentRoomUseCase
import com.nitishsharma.domain.api.interactors.GetAllActiveRoomsUseCase
import com.nitishsharma.domain.api.interactors.SaveUserToDbUseCase
import com.nitishsharma.domain.api.interactors.UpdateRoomAvailableStatusUseCase
import com.nitishsharma.domain.api.interactors.UpdateRoomJoinerIdUseCase
import org.koin.dsl.module

val useCaseModules = module {
    factory { GetAllActiveRoomsUseCase(get()) }
    factory { DeleteCurrentRoomUseCase(get()) }
    factory { SaveUserToDbUseCase(get()) }
    factory { CanJoinRoomUseCase(get()) }
    factory { UpdateRoomAvailableStatusUseCase(get()) }
    factory { UpdateRoomJoinerIdUseCase(get()) }
}