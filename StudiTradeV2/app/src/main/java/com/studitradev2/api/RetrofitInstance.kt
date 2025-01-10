package com.studitradev2.api

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080"

    // Retrofit sans Interceptor (ancienne approche)
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val userApi: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val notesApi: NotesApiService by lazy {
        retrofit.create(NotesApiService::class.java)
    }

    // Retrofit avec Interceptor (nouvelle approche)
    private fun getOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                // Récupérer le token JWT depuis SharedPreferences
                val token = sharedPreferences.getString("jwtToken", null)
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    private fun createRetrofitWithInterceptor(sharedPreferences: SharedPreferences): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient(sharedPreferences))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Fournir les nouvelles instances avec contexte
    fun getAuthApi(context: Context): AuthApiService {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return createRetrofitWithInterceptor(sharedPreferences).create(AuthApiService::class.java)
    }

    fun getUserApi(context: Context): UserApiService {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return createRetrofitWithInterceptor(sharedPreferences).create(UserApiService::class.java)
    }

    fun getNotesApi(context: Context): NotesApiService {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return createRetrofitWithInterceptor(sharedPreferences).create(NotesApiService::class.java)
    }
}
