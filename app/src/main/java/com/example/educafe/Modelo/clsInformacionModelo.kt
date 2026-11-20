package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

@Serializable
data class clsInformacionModelo(
    val nombre_negocio: String,
    val eslogan: String? = null,
    val nosotros: String? = null,
    val mision: String? = null,
    val vision: String? = null,
    val horario: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val correo: String? = null,
    val facebook: String? = null,
    val instagram: String? = null
)
