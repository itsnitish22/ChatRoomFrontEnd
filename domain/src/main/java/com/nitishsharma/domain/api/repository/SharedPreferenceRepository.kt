package com.nitishsharma.domain.api.repository

interface SharedPreferenceRepository {
    var isChatActivityOpen: Boolean
    fun getUIDFromPref(): String?
    fun saveUIDToPrefs(key: String, value: String): Unit
}