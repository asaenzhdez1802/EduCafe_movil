package com.example.educafe.Presentador

import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.clsApiError
import com.example.educafe.Modelo.clsCarrito
import com.example.educafe.Modelo.clsPedidosModelo
import com.example.educafe.Vista.MisPedidosActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// C5 · Mis pedidos activos (pendientes y listos).
// No hay notificaciones push: se recarga al volver a la pantalla y cada 30 s
// mientras está visible, para reflejar cuando el cajero lo marca listo.
class clsMisPedidosPresentador(private val vista: MisPedidosActivity) {

    companion object {
        private const val INTERVALO_ACTUALIZACION = 30_000L
    }

    private val modelo = clsPedidosModelo()
    private val alcance = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var actualizacion: Job? = null

    // onResume: carga inmediata y después cada 30 segundos
    fun iniciar() {
        actualizacion?.cancel()
        actualizacion = alcance.launch {
            var primeraCarga = true
            while (isActive) {
                val seguir = cargarPedidos(primeraCarga)
                primeraCarga = false
                if (!seguir) break
                delay(INTERVALO_ACTUALIZACION)
            }
        }
    }

    // onPause: deja de consultar cuando la pantalla no se ve
    fun detener() {
        actualizacion?.cancel()
    }

    // Devuelve false si ya no tiene sentido seguir consultando (sesión vencida o sin permiso).
    // Los errores se avisan solo en la primera carga, para no repetirlos cada 30 s.
    private suspend fun cargarPedidos(avisarError: Boolean): Boolean {
        // Contrato: haySesion() solo dice si hay credenciales locales;
        // el servidor valida el token (si venció responde 401)
        if (!SesionUsuario.haySesion()) {
            sesionTerminada()
            return false
        }
        try {
            val respuesta = modelo.obtenerPedidosActivos()
            if (respuesta.status == "success") {
                vista.mostrarPedidos(respuesta.data ?: emptyList())
            } else if (avisarError) {
                vista.mostrarMensaje(respuesta.message)
            }
            return true
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            val error = clsApiError.leer(e)
            if (error.codigo == 401) {
                sesionTerminada()
                return false
            }
            if (avisarError) vista.mostrarMensaje(error.mensaje)
            return error.codigo != 403
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
