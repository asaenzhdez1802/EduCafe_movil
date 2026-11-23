package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

@Serializable
data class clsUsuarioSesion(
    val id_usuario: Int,
    val nombre: String,
    val usuario: String,
    val celular: String? = null,
    val rol: String,
    val puntos: Int,
    val token: String,
    val fecha_expira: String
)
