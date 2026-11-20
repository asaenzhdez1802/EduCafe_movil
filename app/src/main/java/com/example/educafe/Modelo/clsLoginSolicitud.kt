package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

// Datos enviados a login.php.
@Serializable
data class clsLoginSolicitud(
    val usuario: String,
    val password: String
)