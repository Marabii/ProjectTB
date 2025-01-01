package com.studitradev2.api

import com.studitradev2.models.UserDTO
import retrofit2.Call
import retrofit2.http.GET

interface UserApiService {
    @GET("/api/protected/getUserInfo")
    fun getUserInfo(): Call<UserDTO>

    @GET("/api/protected/verifyUser")
    fun verifyUser(): Call<Map<String, Boolean>>
}
