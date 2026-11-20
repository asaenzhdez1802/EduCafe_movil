package com.example.educafe.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class dataPedidoRegistrado(
    @SerialName("id_pedido")
    val idPedido: Int = 0,

    @SerialName("folio")
    val folio: String? = null,

    @SerialName("codigo_recogida")
    val codigoRecogida: String? = null,

    @SerialName("total")
    val total: Double = 0.0,

    @SerialName("fecha")
    val fecha: String? = null,

    @SerialName("fecha_vence")
    val fechaVence: String? = null,

    @SerialName("estado")
    val estado: String = "",

    @SerialName("estado_pago")
    val estadoPago: String = ""
)
