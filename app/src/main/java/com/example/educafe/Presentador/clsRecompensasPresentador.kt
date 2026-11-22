package com.example.educafe.Presentador

import com.example.educafe.Modelo.RetrofitClient
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.clsRecompensasModelo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class clsRecompensasPresentador(
    private val vista: VistaRecompensas,
    private val alcance: CoroutineScope
) {

    // 1. Comunicación entre el Presentador y la Vista.
    interface VistaRecompensas {

        fun mostrarCarga(cargando: Boolean)

        fun mostrarDatosRecompensas(
            datos: clsRecompensasModelo
        )

        fun mostrarErrorRecompensas(mensaje: String)

        fun sesionVencidaRecompensas()
    }

    // 2. Evitar consultas duplicadas.
    private var consultando = false

    // 3. Consultar recompensas del cliente.
    fun cargarRecompensas() {

        if (consultando) return

        // Verificar que exista una sesión.
        if (!SesionUsuario.haySesion()) {
            vista.sesionVencidaRecompensas()
            return
        }

        // Comprobar que el usuario sea cliente.
        if (SesionUsuario.obtenerRol() != "cliente") {
            vista.mostrarErrorRecompensas(
                "Esta pantalla es exclusiva para clientes"
            )
            return
        }

        consultando = true
        vista.mostrarCarga(true)

        alcance.launch {

            try {

                // 4. Consultar la API.
                val respuesta =
                    RetrofitClient.apiService
                        .obtenerRecompensas()

                // 5. Verificar el resultado.
                if (
                    respuesta.status == "success" &&
                    respuesta.data != null
                ) {

                    vista.mostrarDatosRecompensas(
                        respuesta.data
                    )

                } else {

                    vista.mostrarErrorRecompensas(
                        respuesta.message.ifBlank {
                            "No se pudieron cargar las recompensas"
                        }
                    )
                }

            } catch (e: HttpException) {

                // 6. Errores HTTP.
                when (e.code()) {

                    401 -> {
                        // RetrofitClient administra el token
                        // y limpia la sesión ante HTTP 401.
                        vista.sesionVencidaRecompensas()
                    }

                    403 -> {
                        vista.mostrarErrorRecompensas(
                            "No tienes permiso para consultar las recompensas"
                        )
                    }

                    else -> {
                        vista.mostrarErrorRecompensas(
                            "Error del servidor (${e.code()})"
                        )
                    }
                }

            } catch (e: IOException) {

                // 7. Errores de conexión.
                vista.mostrarErrorRecompensas(
                    "No se pudo conectar con el servidor. Revisa tu conexión a internet."
                )

            } catch (e: CancellationException) {

                // Respetar la cancelación de corrutinas.
                throw e

            } catch (e: Exception) {

                // 8. Otros errores.
                vista.mostrarErrorRecompensas(
                    "Ocurrió un problema al cargar las recompensas"
                )

            } finally {

                // 9. Finalizar la consulta.
                consultando = false
                vista.mostrarCarga(false)
            }
        }
    }
}
