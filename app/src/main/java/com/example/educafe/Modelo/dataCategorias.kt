package com.example.educafe.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class dataCategorias(
    @SerialName("id_categoria")
    val idCategoria: Int,

    val nombre: String,

    val descripcion: String? = null
) {
    override fun toString(): String {
        return nombre
    }
}