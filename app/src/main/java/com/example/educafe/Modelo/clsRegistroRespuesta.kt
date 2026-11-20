package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

@Serializable
data class clsRegistroRespuesta(
    val id_usuario: Int,
    val nombre: String,
    val usuario: String,
    val celular: String,
    val rol: String,
    val puntos: Int
)