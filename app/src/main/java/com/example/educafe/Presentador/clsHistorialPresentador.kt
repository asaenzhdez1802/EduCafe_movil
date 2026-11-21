package com.example.educafe.Presentador

import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.clsApiError
import com.example.educafe.Modelo.clsCarrito
import com.example.educafe.Modelo.clsPedidosModelo
import com.example.educafe.Vista.HistorialActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// C6 · Historial: pedidos entregados/cancelados y compras en caja.
// Usa SOLO obtener_historial.php (ya viene combinado y sin duplicados).
class clsHistorialPresentador(private val vista: HistorialActivity) {

    private val modelo = clsPedidosModelo()
    private val alcance = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun cargar() {
        // Contrato: haySesion() solo dice si hay credenciales locales;
        // el servidor valida el token (si venció responde 401)
        if (!SesionUsuario.haySesion()) {
            sesionTerminada()
            return
        }
        alcance.launch {
            try {
                val respuesta = modelo.obtenerHistorial()
                if (respuesta.status == "success") {
                    vista.mostrarHistorial(respuesta.data ?: emptyList())
                } else {
                    vista.mostrarMensaje(respuesta.message)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                val error = clsApiError.leer(e)
                if (error.codigo == 401) sesionTerminada() else vista.mostrarMensaje(error.mensaje)
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
