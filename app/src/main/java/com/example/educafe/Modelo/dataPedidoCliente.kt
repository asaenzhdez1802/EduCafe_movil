package com.example.educafe.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class dataPedidoCliente(
    @SerialName("id_pedido")
    val idPedido: Int,

    @SerialName("folio")
    val folio: String? = null,

    @SerialName("codigo_recogida")
    val codigoRecogida: String? = null,

    @SerialName("fecha")
    val fecha: String = "",

    @SerialName("fecha_vence")
    val fechaVence: String? = null,

    @SerialName("total")
    val total: Double = 0.0,

    @SerialName("notas")
    val notas: String? = null,

    @SerialName("estado")
    val estado: String = "",

    @SerialName("motivo_cancelacion")
    val motivoCancelacion: String? = null,

    @SerialName("folio_venta")
    val folioVenta: String? = null,

    @SerialName("num_productos")
    val numProductos: Int? = null,

    @SerialName("estado_pago")
    val estadoPago: String = ""
)
