package com.studitradev2.api

import com.studitradev2.models.AuthenticationRequest
import com.studitradev2.models.AuthenticationResponse
import com.studitradev2.models.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/v1/auth/login")
    fun login(@Body request: AuthenticationRequest): Call<AuthenticationResponse>

    @POST("/api/v1/auth/register")
    fun register(@Body request: RegisterRequest): Call<Unit>
}
