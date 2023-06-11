package com.nitishsharma.domain.api.interactors

import com.nitishsharma.domain.api.repository.SharedPreferenceRepository
import org.koin.core.component.KoinComponent

class IsChatActivityOpenUseCase constructor(private val preferenceRepository: SharedPreferenceRepository) :
    KoinComponent {
    fun setChatActivityOpen(isOpen: Boolean) {
        preferenceRepository.isChatActivityOpen = isOpen
    }

    fun getChatActivityOpen(): Boolean {
        return preferenceRepository.isChatActivityOpen
    }
}