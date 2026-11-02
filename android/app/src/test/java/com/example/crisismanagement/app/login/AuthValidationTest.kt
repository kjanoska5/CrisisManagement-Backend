package com.example.crisismanagement.app.login

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidationTest {

    @Test
    fun `email - blank is empty error`() =
        assertEquals(FieldError.EMPTY, AuthValidation.validateEmail("  "))

    @Test
    fun `email - wrong format is invalid`() {
        assertEquals(FieldError.INVALID_EMAIL, AuthValidation.validateEmail("abc"))
        assertEquals(FieldError.INVALID_EMAIL, AuthValidation.validateEmail("abc@"))
        assertEquals(FieldError.INVALID_EMAIL, AuthValidation.validateEmail("a@b"))
    }

    @Test
    fun `email - valid with surrounding spaces`() =
        assertNull(AuthValidation.validateEmail(" test@okej.com "))

    @Test
    fun `new password - too short`() =
        assertEquals(FieldError.PASSWORD_TOO_SHORT, AuthValidation.validateNewPassword("1234567"))

    @Test
    fun `new password - 8 characters is ok`() =
        assertNull(AuthValidation.validateNewPassword("12345678"))

    @Test
    fun `password repeat - mismatch`() =
        assertEquals(FieldError.PASSWORDS_DIFFER, AuthValidation.validatePasswordRepeat("password1", "password2"))

    @Test
    fun `password repeat - match`() =
        assertNull(AuthValidation.validatePasswordRepeat("password1", "password1"))

    @Test
    fun `full name - single word is incomplete`() =
        assertEquals(FieldError.NAME_INCOMPLETE, AuthValidation.validateFullName("Jan"))

    @Test
    fun `split name - first word is first name, rest is last name`() {
        assertEquals("Jan" to "Kowalski", AuthValidation.splitFullName("Jan Kowalski"))
        assertEquals("Anna" to "Maria Nowak", AuthValidation.splitFullName("  Anna   Maria Nowak "))
        assertNull(AuthValidation.splitFullName("Jan"))
    }
}