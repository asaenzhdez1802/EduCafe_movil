package com.example.educafe.Modelo

data class dataArticuloCarrito(
    val idProducto: Int,
    val nombre: String,
    val precio: Double,
    val imagen: String?,
    val disponibles: Int,
    var cantidad: Int
) {
    fun subtotal(): Double {
        return precio * cantidad
    }
}
