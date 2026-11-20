
package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

@Serializable
data class clsRecompensasModelo(
    val puntos: Int? = null,
    val regla: String,
    val recompensas: List<clsRecompensa>
)

@Serializable
data class clsRecompensa(
    val id_recompensa: Int,
    val nombre: String,
    val descripcion: String,
    val puntos_requeridos: Int,
    val tipo: String,
    val alcanzable: Boolean = false,
    val faltan: Int = 0
)
