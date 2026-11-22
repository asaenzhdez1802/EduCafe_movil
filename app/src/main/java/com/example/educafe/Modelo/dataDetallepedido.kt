package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

// Producto incluido en el detalle de un pedido
@Serializable
data class dataProductoDetalle(
    val id_producto: Int,
    val nombre: String,
    val imagen: String? = null,
    val cantidad: Int,
    val precio_unitario: Double,
    val subtotal: Double
)

// Información recibida de detalle_pedido.php
@Serializable
data class dataDetallePedido(
    val id_pedido: Int,
    val folio: String,
    val codigo_recogida: String,
    val id_cliente: Int,
    val cliente: String,
    val usuario_cliente: String? = null,
    val fecha: String,
    val fecha_vence: String,
    val total: Double,
    val notas: String? = null,
    val estado: String,
    val motivo_cancelacion: String? = null,
    val folio_venta: String? = null,
    val estado_pago: String,
    val productos: List<dataProductoDetalle>
)