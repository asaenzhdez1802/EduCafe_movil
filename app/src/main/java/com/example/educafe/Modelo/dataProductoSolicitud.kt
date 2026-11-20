package com.example.educafe.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class dataProductoSolicitud(
    @SerialName("id_producto")
    val idProducto: Int,

    @SerialName("cantidad")
    val cantidad: Int
)
