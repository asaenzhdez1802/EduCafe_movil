package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

// Formato de respuesta de las API de Edu Café
@Serializable
data class RespuestaApi<T>(
    val status: String,
    val message: String,
    val data: T? = null
)