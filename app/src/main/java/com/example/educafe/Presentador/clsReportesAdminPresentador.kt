package com.example.educafe.Presentador

import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.clsApiError
import com.example.educafe.Modelo.clsReportesAdminModelo
import com.example.educafe.Vista.ReporteAdminActivity
import kotlinx.coroutines.*

class clsReportesAdminPresentador(
    private val vista: ReporteAdminActivity
) {
    private val modelo = clsReportesAdminModelo()
    private val alcance =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var tarea: Job? = null
    private var consulta = 0

    fun cargar(periodo: String, esVentas: Boolean) {
        cancelar()

        // 1. Comprobar la sesión local.
        if (!SesionUsuario.haySesion()) {
            vista.solicitarLogin()
            return
        }

        if (SesionUsuario.obtenerRol() != "admin") {
            vista.mostrarError(
                "No tienes permiso para consultar estos reportes."
            )
            return
        }

        val actual = consulta
        val token = SesionUsuario.obtenerToken()

        vista.mostrarCarga()

        tarea = alcance.launch {
            try {
                // 2. Consultar el reporte correspondiente.
                if (esVentas) {
                    val respuesta = modelo.obtenerVentas(periodo)

                    if (
                        actual != consulta ||
                        token != SesionUsuario.obtenerToken()
                    ) {
                        return@launch
                    }

                    val datos = respuesta.data

                    if (respuesta.status == "success" && datos != null) {
                        vista.mostrarVentas(datos)
                    } else {
                        vista.mostrarError(respuesta.message)
                    }
                } else {
                    val respuesta = modelo.obtenerProductos(periodo)

                    if (
                        actual != consulta ||
                        token != SesionUsuario.obtenerToken()
                    ) {
                        return@launch
                    }

                    val datos = respuesta.data

                    if (respuesta.status == "success" && datos != null) {
                        vista.mostrarProductos(datos)
                    } else {
                        vista.mostrarError(respuesta.message)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // 3. Ignorar errores de solicitudes anteriores.
                if (actual != consulta) {
                    return@launch
                }

                val vigente = SesionUsuario.obtenerToken()

                if (vigente != null && vigente != token) {
                    return@launch
                }

                val error = clsApiError.leer(e)

                if (error.codigo == 401) {
                    vista.solicitarLogin()
                } else {
                    // Incluye 403 y fallos de conexión.
                    vista.mostrarError(error.mensaje)
                }
            }
        }
    }

    fun cancelar() {
        consulta++
        tarea?.cancel()
    }

    fun destruir() {
        cancelar()
        alcance.cancel()
    }
}