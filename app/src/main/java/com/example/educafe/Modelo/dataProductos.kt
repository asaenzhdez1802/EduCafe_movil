package com.example.educafe.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class dataProductos(
    @SerialName("id_producto")
    val idProducto: Int,

    @SerialName("id_categoria")
    val idCategoria: Int,

    val categoria: String,
    val nombre: String,
    val descripcion: String? = null,

    @SerialName("precio_venta")
    val precioVenta: Double,

    val imagen: String? = null,
    val disponibles: Int
)