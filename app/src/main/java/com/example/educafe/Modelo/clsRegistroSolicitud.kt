package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

@Serializable
data class clsRegistroSolicitud(
    val nombre: String,
    val usuario: String,
    val celular: String,
    val password: String,
    val confirmar_password: String
)
