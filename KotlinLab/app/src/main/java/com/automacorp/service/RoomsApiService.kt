package com.automacorp.service

import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoomsApiService {
    @GET("rooms")
    fun readAllRooms(): Call<List<RoomDto>>

    @GET("rooms/{id}")
    fun readOneRoomById(@Path("id") id: Long): Call<RoomDto>

    @PUT("rooms/{id}")
    fun updateRoom(@Path("id") id: Long, @Body room: RoomCommandDto): Call<RoomDto>

    @POST("rooms/{id}")
    fun createRoom(@Path("id") id: Long, @Body room: RoomCommandDto): Call<RoomDto>

    @DELETE("room/{id}")
    fun deleteRoom(@Path("id") id: Long): Call<Any>
}
