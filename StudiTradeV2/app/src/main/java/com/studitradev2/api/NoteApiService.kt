package com.studitradev2.api

import com.studitradev2.models.NoteDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface NotesApiService {
    @GET("/api/notes")
    fun getAllNotes(): Call<List<NoteDTO>>

    @Multipart
    @POST("/protected/notes/upload")
    suspend fun uploadNote(
        @Part demoFile: MultipartBody.Part,
        @Part title: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part price: MultipartBody.Part,
        @Part isDigital: MultipartBody.Part
    ): retrofit2.Response<Unit>

}
