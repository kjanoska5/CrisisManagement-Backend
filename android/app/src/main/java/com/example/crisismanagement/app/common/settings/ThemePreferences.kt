package com.example.crisismanagement.app.common.settings

import android.content.Context

class ThemePreferences(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun load(): ThemeMode {
        val saved = preferences.getString(KEY_THEME_MODE, null)
        return ThemeMode.entries.firstOrNull { it.name == saved } ?: DEFAULT_MODE
    }

    fun save(mode: ThemeMode) {
        preferences.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    companion object {
        private const val FILE_NAME = "app_settings"
        private const val KEY_THEME_MODE = "theme_mode"
        val DEFAULT_MODE = ThemeMode.DARK
    }
}