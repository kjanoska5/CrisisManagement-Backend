package com.example.crisismanagement.app.common.auth

import android.content.Context
import com.example.crisismanagement.app.enums.Role

class SessionManager(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun load(): UserSession? {
        val userId = preferences.getString(KEY_USER_ID, null) ?: return null
        val roleName = preferences.getString(KEY_ROLE, null)
        val role = Role.entries.firstOrNull { it.name == roleName } ?: return null
        return UserSession(
            userId = userId,
            role = role,
            displayName = preferences.getString(KEY_DISPLAY_NAME, null),
            email = preferences.getString(KEY_EMAIL, null),
            signedInWithGoogle = preferences.getBoolean(KEY_GOOGLE, false)
        )
    }

    fun save(session: UserSession) {
        preferences.edit()
            .putString(KEY_USER_ID, session.userId)
            .putString(KEY_ROLE, session.role.name)
            .putString(KEY_DISPLAY_NAME, session.displayName)
            .putString(KEY_EMAIL, session.email)
            .putBoolean(KEY_GOOGLE, session.signedInWithGoogle)
            .apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val FILE_NAME = "session"
        const val KEY_USER_ID = "user_id"
        const val KEY_ROLE = "role"
        const val KEY_DISPLAY_NAME = "display_name"
        const val KEY_EMAIL = "email"
        const val KEY_GOOGLE = "signed_in_with_google"
    }
}