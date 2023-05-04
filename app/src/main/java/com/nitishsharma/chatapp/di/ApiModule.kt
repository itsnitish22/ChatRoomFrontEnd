package com.nitishsharma.chatapp.di

import com.nitishsharma.data.api.servcies.ChatRoomAPIService
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    fun provideApiService(retrofit: Retrofit): ChatRoomAPIService {
        return retrofit.create(ChatRoomAPIService::class.java)
    }
    single { provideApiService(get()) }
}