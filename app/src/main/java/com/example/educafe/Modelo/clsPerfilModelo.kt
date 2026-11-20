package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

@Serializable
data class clsPerfilModelo(
    val id_usuario: Int,
    val nombre: String,
    val usuario: String,
    val celular: String,
    val rol: String,
    val puntos: Int,
    val siguiente_recompensa: clsSiguienteRecompensa? = null,
    val pedidos_por_cobrar: Int = 0,
    val ultimas_compras: List<clsUltimaCompra> = emptyList()
)

@Serializable
data class clsSiguienteRecompensa(
    val nombre: String,
    val puntos_requeridos: Int,
    val faltan: Int
)

@Serializable
data class clsUltimaCompra(
    val folio: String,
    val fecha: String,
    val total: Double,
    val puntos_ganados: Int
)
