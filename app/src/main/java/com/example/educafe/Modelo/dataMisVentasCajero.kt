package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

@Serializable
data class dataVentaCajero(
    val id_venta: Int,
    val folio: String,
    val fecha: String,
    val total: Double,
    val estado: String,
    val puntos_ganados: Int,
    val folio_pedido: String? = null,
    val cliente: String? = null,
    val origen: String
)

@Serializable
data class dataMisVentasCajero(
    val total_cobrado: Double,
    val ventas: List<dataVentaCajero>
)