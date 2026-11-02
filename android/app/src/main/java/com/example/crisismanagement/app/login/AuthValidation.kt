package com.example.crisismanagement.app.login

import androidx.annotation.StringRes
import com.example.crisismanagement.R

const val MIN_PASSWORD_LENGTH = 8

enum class FieldError {
    EMPTY,
    INVALID_EMAIL,
    PASSWORD_TOO_SHORT,
    PASSWORDS_DIFFER,
    NAME_INCOMPLETE
}

object AuthValidation {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateEmail(email: String): FieldError? = when {
        email.isBlank() -> FieldError.EMPTY
        !EMAIL_REGEX.matches(email.trim()) -> FieldError.INVALID_EMAIL
        else -> null
    }

    fun validateLoginPassword(password: String): FieldError? =
        if (password.isBlank()) FieldError.EMPTY else null

    fun validateNewPassword(password: String): FieldError? = when {
        password.isBlank() -> FieldError.EMPTY
        password.length < MIN_PASSWORD_LENGTH -> FieldError.PASSWORD_TOO_SHORT
        else -> null
    }

    fun validatePasswordRepeat(password: String, repeat: String): FieldError? = when {
        repeat.isEmpty() -> FieldError.EMPTY
        repeat != password -> FieldError.PASSWORDS_DIFFER
        else -> null
    }

    fun validateFullName(fullName: String): FieldError? = when {
        fullName.isBlank() -> FieldError.EMPTY
        splitFullName(fullName) == null -> FieldError.NAME_INCOMPLETE
        else -> null
    }

    /** Pierwsze slowo = Imię, reszta = nazwisko. Null kiedy mniej niz dwa słowa */
    fun splitFullName(fullName: String): Pair<String, String>? {
        val parts = fullName.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (parts.size < 2) return null
        return parts.first() to parts.drop(1).joinToString(" ")
    }
}

@StringRes
fun FieldError.messageRes(): Int = when (this) {
    FieldError.EMPTY -> R.string.error_field_empty
    FieldError.INVALID_EMAIL -> R.string.error_email_invalid
    FieldError.PASSWORD_TOO_SHORT -> R.string.error_password_short
    FieldError.PASSWORDS_DIFFER -> R.string.error_passwords_differ
    FieldError.NAME_INCOMPLETE -> R.string.error_name_incomplete
}