package com.example.educafe.Modelo

import com.example.educafe.interfaceApi.ApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {

    // 1. Dirección de las API en el hosting
    private const val BASE_URL =
        "https://manuelsh.somos4a.org/educafe_api/"

    // 2. Configuración para leer las respuestas JSON
    private val json = Json {
        ignoreUnknownKeys = true
    }

    // 3. Crear la conexión Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
        .build()

    // 4. Crear el servicio para consultar las API
    val apiService: ApiService =
        retrofit.create(ApiService::class.java)
}