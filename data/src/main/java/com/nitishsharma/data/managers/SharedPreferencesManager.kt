package com.nitishsharma.data.managers

import android.content.Context
import android.content.SharedPreferences
import org.koin.core.component.KoinComponent

class SharedPreferencesManager constructor(appContext: Context) : KoinComponent {
    private var prefs: SharedPreferences
    private val prefName = "com.nitishsharma.chatapp"

    init {
        prefs = appContext.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean? =
        prefs.getBoolean(key, defaultValue)

    fun setBoolean(key: String, defaultValue: Boolean) {
        prefs.edit().run {
            putBoolean(key, defaultValue)
            commit()
        }
    }

    fun getString(key: String, defValue: String? = null): String? = prefs.getString(key, defValue)

    fun setString(key: String, value: String?) {
        prefs.edit().run {
            putString(key, value)
            commit()
        }
    }
}