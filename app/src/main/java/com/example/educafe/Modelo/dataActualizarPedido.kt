package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

// Datos enviados a actualizar_pedido.php
@Serializable
data class dataActualizarPedido(
    val id_pedido: Int,
    val accion: String
)

// Datos que devuelve la API cuando
// el pedido se actualiza correctamente
@Serializable
data class dataPedidoActualizado(
    val id_pedido: Int,
    val estado: String
)