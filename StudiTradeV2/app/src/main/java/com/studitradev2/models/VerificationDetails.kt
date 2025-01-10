package com.studitradev2.models

data class VerificationDetails(
    val id: Long,
    val isEmailVerified: Boolean = false,
    val isPhoneNumberVerified: Boolean = false
)
