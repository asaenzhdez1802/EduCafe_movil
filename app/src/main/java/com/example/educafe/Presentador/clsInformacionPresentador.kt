package com.example.educafe.Presentador

import com.example.educafe.Modelo.RetrofitClient
import com.example.educafe.Modelo.clsInformacionModelo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class clsInformacionPresentador(
    private val vista: VistaInformacion,
    private val alcance: CoroutineScope
) {

    interface VistaInformacion {

        fun mostrarCargaInformacion(cargando: Boolean)

        fun mostrarDatosInformacion(
            datos: clsInformacionModelo
        )

        fun mostrarErrorInformacion(mensaje: String)
    }

    private var consultando = false

    fun cargarInformacion() {

        if (consultando) return

        consultando = true
        vista.mostrarCargaInformacion(true)

        alcance.launch {

            try {

                val respuesta =
                    RetrofitClient.apiService
                        .obtenerInformacion()

                if (
                    respuesta.status == "success" &&
                    respuesta.data != null
                ) {

                    vista.mostrarDatosInformacion(
                        respuesta.data
                    )

                } else {

                    vista.mostrarErrorInformacion(
                        respuesta.message.ifBlank {
                            "No se pudo cargar la información"
                        }
                    )
                }

            } catch (e: HttpException) {

                when (e.code()) {

                    401 -> {
                        vista.mostrarErrorInformacion(
                            "La sesión no es válida. Inicia sesión nuevamente."
                        )
                    }

                    403 -> {
                        vista.mostrarErrorInformacion(
                            "No tienes permiso para consultar la información"
                        )
                    }

                    404 -> {
                        vista.mostrarErrorInformacion(
                            "No se encontró el servicio de información"
                        )
                    }

                    else -> {
                        vista.mostrarErrorInformacion(
                            "Error del servidor (${e.code()})"
                        )
                    }
                }

            } catch (e: IOException) {

                vista.mostrarErrorInformacion(
                    "No se pudo conectar con el servidor. Revisa tu conexión a internet."
                )

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                vista.mostrarErrorInformacion(
                    "Ocurrió un problema al consultar la información"
                )

            } finally {

                consultando = false
                vista.mostrarCargaInformacion(false)
            }
        }
    }
}
