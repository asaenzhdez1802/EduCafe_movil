package com.example.educafe.Presentador

import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.clsApiError
import com.example.educafe.Modelo.clsCarrito
import com.example.educafe.Modelo.clsPedidosModelo
import com.example.educafe.Modelo.dataProductoSolicitud
import com.example.educafe.Modelo.dataSolicitudPedido
import com.example.educafe.Vista.ConfirmarPedidoActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// C3 · Confirmar pedido. ÚNICA pantalla que registra un pedido en el servidor.
class clsConfirmarPedidoPresentador(private val vista: ConfirmarPedidoActivity) {

    private val modelo = clsPedidosModelo()
    private val alcance = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var enviando = false

    fun mostrarResumen() {
        if (clsCarrito.estaVacio()) {
            vista.mostrarMensaje("Tu pedido está vacío")
            vista.volverAlPedido()
            return
        }
        vista.mostrarTotal(clsCarrito.subtotal())
    }

    fun estaEnviando(): Boolean {
        return enviando
    }

    fun confirmar() {
        // Evita doble envío si se toca dos veces
        if (enviando) return

        if (clsCarrito.estaVacio()) {
            vista.mostrarMensaje("Tu pedido está vacío")
            vista.volverAlPedido()
            return
        }
        if (!SesionUsuario.haySesion()) {
            sesionTerminada()
            return
        }

        // Mismo UUID si es un reintento del mismo contenido; nuevo si el carrito cambió.
        // El mockup no tiene campo de notas: se envían vacías.
        val solicitud = dataSolicitudPedido(
            idSolicitud = clsCarrito.obtenerIdSolicitud(),
            notas = "",
            productos = clsCarrito.obtenerArticulos().map { dataProductoSolicitud(it.idProducto, it.cantidad) }
        )

        enviando = true
        vista.mostrarEnviando(true)

        alcance.launch {
            try {
                val respuesta = modelo.registrarPedido(solicitud)
                val pedido = respuesta.data
                if (respuesta.status == "success" && pedido != null) {
                    // Vaciar el carrito SOLO después de una respuesta exitosa
                    clsCarrito.vaciar()
                    vista.pedidoRegistrado(pedido)
                } else {
                    // Errores de negocio con HTTP 200: sin existencias, máximo 10 piezas,
                    // 3 pedidos sin pagar, producto no disponible hoy...
                    vista.mostrarMensaje(respuesta.message)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                val error = clsApiError.leer(e)
                when {
                    error.codigo == 401 -> sesionTerminada()
                    error.codigo == 409 -> {
                        // Ese UUID ya se usó con otro contenido: se genera uno nuevo
                        clsCarrito.nuevaSolicitud()
                        vista.mostrarMensaje("Tu pedido cambió. Toca Confirmar pedido de nuevo")
                    }
                    error.sinRespuesta -> {
                        // No sabemos si el servidor lo registró: se conserva el UUID
                        // para que el reintento NO duplique el pedido.
                        vista.mostrarMensaje(error.mensaje + ". Toca Confirmar pedido para reintentar; no se duplicará")
                    }
                    else -> vista.mostrarMensaje(error.mensaje)
                }
            } finally {
                enviando = false
                vista.mostrarEnviando(false)
            }
        }
    }

    fun destruir() {
        alcance.cancel()
    }

    // Contrato: sin sesión o 401 se vacía el carrito y la vista pide Login
    private fun sesionTerminada() {
        clsCarrito.vaciar()
        vista.sesionVencida()
    }
}
