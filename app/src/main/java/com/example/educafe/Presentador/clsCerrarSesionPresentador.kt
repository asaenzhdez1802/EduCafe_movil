package com.example.educafe.Presentador

import com.example.educafe.Modelo.RetrofitClient
import com.example.educafe.Modelo.SesionUsuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import retrofit2.HttpException
import java.io.IOException

class clsCerrarSesionPresentador(
    private val vista: VistaCerrarSesion,
    private val alcance: CoroutineScope
) {

    interface VistaCerrarSesion {
        fun mostrarCargaSalida(cargando: Boolean)
        fun mostrarErrorSalida(mensaje: String)
        fun sesionCerrada()
    }

    private var cerrando = false

    fun cerrarSesion() {

        // 1. Evitar solicitudes repetidas.
        if (cerrando) return

        // 2. Obtener el token de la sesión actual.
        val token = SesionUsuario.obtenerToken()

        if (token.isNullOrBlank()) {
            vista.sesionCerrada()
            return
        }

        cerrando = true
        vista.mostrarCargaSalida(true)

        alcance.launch {
            try {

                // 3. Solicitar al servidor que revoque el token.
                // Retrofit agrega Authorization automáticamente.
                RetrofitClient.apiService.cerrarSesion(
                    buildJsonObject {}
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: HttpException) {
                // Si el token ya venció, la sesión local
                // también debe quedar cerrada.
            } catch (e: IOException) {
                // No fue posible contactar con el servidor.
                vista.mostrarErrorSalida(
                    "No se pudo confirmar el cierre en el servidor."
                )

            } catch (e: Exception) {
                vista.mostrarErrorSalida(
                    "Ocurrió un problema al cerrar sesión."
                )

            } finally {

                // 4. Limpiar únicamente la sesión correspondiente.
                SesionUsuario.limpiarSiCoincide(token)

                cerrando = false
                vista.mostrarCargaSalida(false)
                vista.sesionCerrada()
            }
        }
    }
}
