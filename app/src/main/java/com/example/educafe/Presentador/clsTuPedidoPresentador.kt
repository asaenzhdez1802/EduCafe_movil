package com.example.educafe.Presentador

import com.example.educafe.Modelo.SesionUsuario
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
        // Como en el prototipo: en 1 no hace nada
        if (clsCarrito.disminuir(articulo.idProducto)) {
            cargar()
        }
    }

    fun continuar() {
        if (clsCarrito.estaVacio()) {
            vista.mostrarMensaje("Agrega al menos un producto para continuar")
            return
        }
        // Contrato: sin sesión, Login con el aviso "Inicia sesión para pagar"
        if (!SesionUsuario.haySesion()) {
            vista.irALoginParaPagar()
            return
        }
        vista.irAConfirmar()
    }

    // Regresa a P2 sin vaciar el carrito
    fun seguirComprando() {
        vista.cerrar()
    }

    // Vacía el carrito y regresa a P2
    fun cancelarCarrito() {
        clsCarrito.vaciar()
        vista.cerrar()
    }
}
