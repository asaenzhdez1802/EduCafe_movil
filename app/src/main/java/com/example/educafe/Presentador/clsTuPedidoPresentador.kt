package com.example.educafe.Presentador

import com.example.educafe.Modelo.clsCarrito
import com.example.educafe.Modelo.dataArticuloCarrito
import com.example.educafe.Vista.TuPedidoActivity


class clsTuPedidoPresentador(private val vista: TuPedidoActivity) {

    fun cargar() {
        val articulos = clsCarrito.obtenerArticulos()
        if (articulos.isEmpty()) {
            vista.mostrarVacio()
        } else {
            vista.mostrarArticulos(articulos, clsCarrito.totalPiezas(), clsCarrito.subtotal())
        }
    }

    fun aumentar(articulo: dataArticuloCarrito) {
        val error = clsCarrito.aumentar(articulo.idProducto)
        if (error != null) {
            vista.mostrarMensaje(error)
        }
        cargar()
    }

    fun disminuir(articulo: dataArticuloCarrito) {
        // Si ya está en 1, en lugar de llegar a 0 se pregunta si se elimina
        if (clsCarrito.disminuir(articulo.idProducto)) {
            cargar()
        } else {
            vista.preguntarEliminar(articulo)
        }
    }

    fun eliminar(articulo: dataArticuloCarrito) {
        clsCarrito.eliminar(articulo.idProducto)
        cargar()
    }

    fun continuar() {
        if (clsCarrito.estaVacio()) {
            vista.mostrarMensaje("Agrega al menos un producto para continuar")
            return
        }
        vista.irAConfirmar()
    }

    fun cancelarCarrito() {
        clsCarrito.vaciar()
        vista.volverAlMenu()
    }
}
