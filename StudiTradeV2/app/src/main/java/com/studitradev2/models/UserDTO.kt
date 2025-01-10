package com.studitradev2.models

data class UserDTO(
    val id: Long,
    val username: String,
    val phoneNumber: String,
    val email: String,
    val verificationDetails: VerificationDetails? = null
)
