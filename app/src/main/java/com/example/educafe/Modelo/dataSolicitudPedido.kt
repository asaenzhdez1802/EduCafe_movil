package com.example.educafe.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class dataSolicitudPedido(
    @SerialName("id_solicitud")
    val idSolicitud: String,

    @SerialName("notas")
    val notas: String,

    @SerialName("productos")
    val productos: List<dataProductoSolicitud>
)
