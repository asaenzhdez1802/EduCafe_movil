package com.example.educafe.Modelo

import java.util.UUID

object clsCarrito {

    const val MAXIMO_POR_PRODUCTO = 10

    private val articulos = mutableListOf<dataArticuloCarrito>()
    private var notas: String = ""

    private var idSolicitud: String? = null


    fun agregar(idProducto: Int, nombre: String, precio: Double, imagen: String?, disponibles: Int): String? {
        if (disponibles <= 0) {
            return "$nombre está agotado por ahora"
        }
        val existente = buscar(idProducto)
        if (existente == null) {
            articulos.add(dataArticuloCarrito(idProducto, nombre, precio, imagen, disponibles, 1))
            contenidoCambiado()
            return null
        }
        return aumentar(idProducto)
    }

    fun aumentar(idProducto: Int): String? {
        val articulo = buscar(idProducto) ?: return "Ese producto ya no está en tu pedido"
        val limite = minOf(MAXIMO_POR_PRODUCTO, articulo.disponibles)
        if (articulo.cantidad >= limite) {
            if (limite == MAXIMO_POR_PRODUCTO) {
                return "Máximo $MAXIMO_POR_PRODUCTO piezas de un mismo producto"
            }
            return "Solo hay ${articulo.disponibles} disponibles de ${articulo.nombre}"
        }
        articulo.cantidad++
        contenidoCambiado()
        return null
    }

    fun disminuir(idProducto: Int): Boolean {
        val articulo = buscar(idProducto) ?: return false
        if (articulo.cantidad <= 1) {
            return false
        }
        articulo.cantidad--
        contenidoCambiado()
        return true
    }

    fun eliminar(idProducto: Int) {
        articulos.removeAll { it.idProducto == idProducto }
        contenidoCambiado()
    }

    fun obtenerArticulos(): List<dataArticuloCarrito> {
        return articulos.map { it.copy() }
    }

    fun totalPiezas(): Int {
        return articulos.sumOf { it.cantidad }
    }

    fun subtotal(): Double {
        return articulos.sumOf { it.subtotal() }
    }

    fun estaVacio(): Boolean {
        return articulos.isEmpty()
    }

    fun obtenerNotas(): String {
        return notas
    }

    fun cambiarNotas(nuevasNotas: String) {
        val limpias = nuevasNotas.trim()
        if (limpias != notas) {
            notas = limpias
            contenidoCambiado()
        }
    }

    fun obtenerIdSolicitud(): String {
        val actual = idSolicitud ?: UUID.randomUUID().toString()
        idSolicitud = actual
        return actual
    }

    fun nuevaSolicitud() {
        idSolicitud = null
    }


    fun vaciar() {
        articulos.clear()
        notas = ""
        idSolicitud = null
    }

    private fun buscar(idProducto: Int): dataArticuloCarrito? {
        return articulos.find { it.idProducto == idProducto }
    }

    private fun contenidoCambiado() {
        idSolicitud = null
    }
}
