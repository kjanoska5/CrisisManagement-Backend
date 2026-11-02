package com.example.crisismanagement.app.common.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

sealed interface GoogleSignInResult {
    data class Success(
        val idToken: String,
        val displayName: String?,
        val email: String
    ) : GoogleSignInResult
    data object Cancelled : GoogleSignInResult
    data class Failure(val message: String) : GoogleSignInResult
}

class GoogleIdTokenProvider {
    suspend fun requestIdToken(activityContext: Context): GoogleSignInResult {
        if (!GoogleAuthConfig.isConfigured) {
            return GoogleSignInResult.Failure("Missing GOOGLE_WEB_CLIENT_ID in local.properties")
        }

        val option = GetSignInWithGoogleOption.Builder(GoogleAuthConfig.webClientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()

        return try {
            val response = CredentialManager.create(activityContext)
                .getCredential(activityContext, request)
            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                GoogleSignInResult.Success(
                    idToken = googleCredential.idToken,
                    displayName = googleCredential.displayName,
                    email = googleCredential.id
                )
            } else {
                GoogleSignInResult.Failure("Unexpected credential type")
            }
        } catch (e: GetCredentialCancellationException) {
            GoogleSignInResult.Cancelled
        } catch (e: NoCredentialException) {
            GoogleSignInResult.Failure("No Google account available on this device")
        } catch (e: GetCredentialException) {
            GoogleSignInResult.Failure(e.message ?: "Google sign-in failed")
        } catch (e: GoogleIdTokenParsingException) {
            GoogleSignInResult.Failure("Invalid Google token")
        }
    }
}