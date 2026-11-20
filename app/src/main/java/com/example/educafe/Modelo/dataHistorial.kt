package com.example.educafe.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class dataHistorial(
    @SerialName("id_pedido")
    val idPedido: Int? = null,

    @SerialName("id_venta")
    val idVenta: Int? = null,

    @SerialName("folio")
    val folio: String? = null,

    @SerialName("codigo_recogida")
    val codigoRecogida: String? = null,

    @SerialName("fecha")
    val fecha: String = "",

    @SerialName("total")
    val total: Double = 0.0,

    @SerialName("estado")
    val estado: String = "",

    @SerialName("motivo_cancelacion")
    val motivoCancelacion: String? = null,

    @SerialName("folio_venta")
    val folioVenta: String? = null,

    @SerialName("puntos_ganados")
    val puntosGanados: Int = 0,

    @SerialName("origen")
    val origen: String = "app",

    @SerialName("estado_pago")
    val estadoPago: String = ""
)
