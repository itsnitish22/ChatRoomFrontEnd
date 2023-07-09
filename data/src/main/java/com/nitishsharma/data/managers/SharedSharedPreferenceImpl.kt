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

    override fun getUIDFromPref(): String? {
        return sharedPreferencesHelper.getString("UserUID")
    }

    override fun saveUIDToPrefs(key: String, value: String) {
        return sharedPreferencesHelper.setString(key, value)
    }
}