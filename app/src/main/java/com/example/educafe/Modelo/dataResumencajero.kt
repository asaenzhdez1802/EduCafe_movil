package com.example.educafe.Modelo

import kotlinx.serialization.Serializable

@Serializable
data class dataResumenCajero(
    val num_ventas_hoy: Int,
    val total_vendido_hoy: Double,
    val ticket_promedio: Double,
    val pedidos_por_preparar: Int,
    val pedidos_listos: Int
)