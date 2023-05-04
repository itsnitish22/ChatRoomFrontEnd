package com.nitishsharma.chatapp.di

import com.nitishsharma.data.api.servcies.ChatRoomAPIRepositoryImpl
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import org.koin.dsl.module

val repositoryModule = module(override = true) {
    factory<ChatRoomAPIRepository> { ChatRoomAPIRepositoryImpl() }
}