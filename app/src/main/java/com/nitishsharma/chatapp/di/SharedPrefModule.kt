package com.nitishsharma.chatapp.di

import com.nitishsharma.data.managers.SharedPreferencesManager
import com.nitishsharma.data.managers.SharedSharedPreferenceImpl
import com.nitishsharma.domain.api.repository.SharedPreferenceRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val sharedPrefModule = module {
    single {
        SharedPreferencesManager(androidApplication())
    }
    single<SharedPreferenceRepository> {
        SharedSharedPreferenceImpl(get())
    }
}