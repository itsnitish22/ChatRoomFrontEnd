package com.nitishsharma.chatapp.di

import com.nitishsharma.data.api.servcies.ChatRoomAPIRepositoryImpl
import com.nitishsharma.data.managers.SharedSharedPreferenceImpl
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import com.nitishsharma.domain.api.repository.SharedPreferenceRepository
import org.koin.dsl.module

val repositoryModule = module(override = true) {
    factory<ChatRoomAPIRepository> { ChatRoomAPIRepositoryImpl() }
    factory<SharedPreferenceRepository> { SharedSharedPreferenceImpl(get()) }
}