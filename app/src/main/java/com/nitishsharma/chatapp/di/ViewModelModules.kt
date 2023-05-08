package com.nitishsharma.chatapp.di

import com.nitishsharma.chatapp.chats.ChatActivityViewModel
import com.nitishsharma.chatapp.main.home.HomeFragmentViewModel
import com.nitishsharma.chatapp.main.onboarding.OnboardingFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { HomeFragmentViewModel() }
    viewModel { OnboardingFragmentViewModel() }
    viewModel { ChatActivityViewModel() }
}