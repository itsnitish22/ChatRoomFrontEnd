package com.nitishsharma.chatapp.di

import com.nitishsharma.domain.api.interactors.DeleteCurrentRoomUseCase
import com.nitishsharma.domain.api.interactors.GetAllActiveRoomsUseCase
import org.koin.dsl.module

val useCaseModules = module {
    factory { GetAllActiveRoomsUseCase(get()) }
    factory { DeleteCurrentRoomUseCase(get()) }
}