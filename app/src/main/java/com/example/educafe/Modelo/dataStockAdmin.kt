package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

@Serializable
data class dataStockAdmin(
    val id_producto: Int,
    val codigo: String,
    val nombre: String,
    val stock: Int,
    val stock_reservado: Int,
    val stock_minimo: Int,
    val disponibles: Int,
    val estado_stock: String
)