package com.example.educafe.Presentador

import com.example.educafe.Modelo.RetrofitClient
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.clsPerfilModelo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class clsMiPanelPresentador(
    private val vista: VistaMiPanel,
    private val alcance: CoroutineScope
) {

    // Comunicación entre Presentador y Activity.
    interface VistaMiPanel {

        fun mostrarCarga(cargando: Boolean)

        fun mostrarPerfil(datos: clsPerfilModelo)

        fun mostrarError(mensaje: String)

        fun sesionVencida()
    }

    private var consultando = false

    fun cargarPerfil() {

        // 1. Evitar consultas repetidas.
        if (consultando) return

        // 2. Verificar si existe una sesión.
        if (!SesionUsuario.haySesion()) {
            vista.sesionVencida()
            return
        }

        // 3. Permitir únicamente clientes.
        if (SesionUsuario.obtenerRol() != "cliente") {
            vista.mostrarError(
                "Esta pantalla es exclusiva para clientes"
            )
            return
        }

        consultando = true
        vista.mostrarCarga(true)

        alcance.launch {
            try {

                // 4. Consultar la API con el token guardado.
                val respuesta =
                    RetrofitClient.apiService.obtenerPerfil()

                // 5. Revisar la respuesta del servidor.
                if (respuesta.status == "success" &&
                    respuesta.data != null
                ) {

                    vista.mostrarPerfil(respuesta.data)

                } else {

                    vista.mostrarError(
                        respuesta.message.ifBlank {
                            "No se pudo cargar el perfil"
                        }
                    )
                }

            } catch (e: HttpException) {

                when (e.code()) {

                    401 -> {
                        // El interceptor compartido ya limpia
                        // el token cuando recibe HTTP 401.
                        vista.sesionVencida()
                    }

                    403 -> {
                        vista.mostrarError(
                            "No tienes permiso para consultar este perfil"
                        )
                    }

                    else -> {
                        val mensajeServidor = try {
                            val cuerpo = e.response()
                                ?.errorBody()
                                ?.string()

                            if (cuerpo.isNullOrBlank()) {
                                ""
                            } else {
                                JSONObject(cuerpo)
                                    .optString("message", "")
                            }
                        } catch (error: Exception) {
                            ""
                        }

                        vista.mostrarError(
                            mensajeServidor.ifBlank {
                                "Error del servidor (${e.code()})"
                            }
                        )
                    }
                }

            } catch (e: IOException) {

                vista.mostrarError(
                    "No se pudo conectar con el servidor. Revisa tu internet."
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                vista.mostrarError(
                    "Ocurrió un problema al cargar el perfil"
                )

            } finally {

                consultando = false
                vista.mostrarCarga(false)
            }
        }
    }
}
