package com.example.educafe.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class dataResumenAdmin(
    val periodo: String,
    val desde: String,

    @SerialName("hasta_exclusivo")
    val hastaExclusivo: String,

    @SerialName("num_ventas")
    val numVentas: Int,

    @SerialName("total_vendido")
    val totalVendido: Double,

    @SerialName("variacion_porcentaje")
    val variacion: Double?,

    val ventas: List<dataVentaAdmin>,

    @SerialName("por_dia")
    val porDia: List<dataVentaDia>
)

@Serializable
data class dataVentaAdmin(
    @SerialName("id_venta")
    val idVenta: Int,

    val folio: String,
    val fecha: String,
    val total: Double,
    val cliente: String? = null,

    @SerialName("folio_pedido")
    val folioPedido: String? = null,

    @SerialName("codigo_recogida")
    val codigoRecogida: String? = null,

    val origen: String
)

@Serializable
data class dataVentaDia(
    val dia: String,
    val total: Double
)

@Serializable
data class dataMasVendidos(
    val periodo: String,
    val desde: String,

    @SerialName("hasta_exclusivo")
    val hastaExclusivo: String,

    val productos: List<dataProductoVendido>,
    val categorias: List<dataIngresoCategoria>
)

@Serializable
data class dataProductoVendido(
    @SerialName("id_producto")
    val idProducto: Int,

    val nombre: String,
    val imagen: String? = null,
    val unidades: Int,
    val importe: Double
)

@Serializable
data class dataIngresoCategoria(
    @SerialName("id_categoria")
    val idCategoria: Int,

    val nombre: String,
    val importe: Double,
    val porcentaje: Double
)