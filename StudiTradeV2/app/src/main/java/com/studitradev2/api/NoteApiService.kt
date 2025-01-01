package com.studitradev2.api

import com.studitradev2.models.NoteDTO
import retrofit2.Call
import retrofit2.http.GET

interface NotesApiService {
    @GET("/api/notes")
    fun getAllNotes(): Call<List<NoteDTO>>
}
