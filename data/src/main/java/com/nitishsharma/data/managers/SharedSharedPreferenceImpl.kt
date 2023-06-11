package com.nitishsharma.data.managers

import com.nitishsharma.domain.api.repository.SharedPreferenceRepository
import org.koin.core.component.KoinComponent

class SharedSharedPreferenceImpl constructor(private val sharedPreferencesHelper: SharedPreferencesManager) :
    SharedPreferenceRepository, KoinComponent {
    override var isChatActivityOpen: Boolean
        get() = sharedPreferencesHelper.getBoolean("IS_CHAT_ACTIVITY_OPEN", false) ?: false
        set(value) {
            sharedPreferencesHelper.setBoolean("IS_CHAT_ACTIVITY_OPEN", value)
        }
}