package com.nitishsharma.chatapp.di

import com.nitishsharma.domain.api.interactors.AddRoomToOtherRoomsArrayUseCase
import com.nitishsharma.domain.api.interactors.CanJoinRoomUseCase
import com.nitishsharma.domain.api.interactors.CheckIfUserExistsUseCase
import com.nitishsharma.domain.api.interactors.DeleteCurrentRoomUseCase
import com.nitishsharma.domain.api.interactors.GetAllActiveRoomsUseCase
import com.nitishsharma.domain.api.interactors.GetRoomDetailsUseCase
import com.nitishsharma.domain.api.interactors.GetUserAvatarUseCase
import com.nitishsharma.domain.api.interactors.IsChatActivityOpenUseCase
import com.nitishsharma.domain.api.interactors.SaveUserToDbUseCase
import com.nitishsharma.domain.api.interactors.UpdateRoomAvailableStatusUseCase
import com.nitishsharma.domain.api.interactors.UpdateRoomJoinerIdUseCase
import org.koin.dsl.module

val useCaseModules = module {
    factory { GetAllActiveRoomsUseCase(get()) }
    factory { DeleteCurrentRoomUseCase(get()) }
    factory { SaveUserToDbUseCase(get(), get()) }
    factory { CanJoinRoomUseCase(get()) }
    factory { UpdateRoomAvailableStatusUseCase(get()) }
    factory { UpdateRoomJoinerIdUseCase(get()) }
    factory { GetUserAvatarUseCase(get()) }
    factory { AddRoomToOtherRoomsArrayUseCase(get()) }
    factory { GetRoomDetailsUseCase(get()) }
    factory { IsChatActivityOpenUseCase(get()) }
    factory { CheckIfUserExistsUseCase(get()) }
}