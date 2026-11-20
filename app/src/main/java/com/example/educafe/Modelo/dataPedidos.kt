package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

// Datos de los pedidos de la app
@Serializable
data class dataPedidos(
    val id_pedido: Int,
    val folio: String,
    val codigo_recogida: String,
    val id_cliente: Int,
    val cliente: String,
    val fecha: String,
    val fecha_vence: String,
    val total: Double,
    val notas: String? = null,
    val estado: String,
    val motivo_cancelacion: String? = null,
    val folio_venta: String? = null,
    val num_productos: Int? = null,
    val estado_pago: String
)