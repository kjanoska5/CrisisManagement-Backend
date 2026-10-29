package com.example.crisismanagement.app.common.auth

import com.example.crisismanagement.BuildConfig

object GoogleAuthConfig {
    val webClientId: String = BuildConfig.GOOGLE_WEB_CLIENT_ID

    val isConfigured: Boolean
        get() = webClientId.isNotBlank()
}