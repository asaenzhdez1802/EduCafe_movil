package com.example.educafe.Presentador

import com.example.educafe.Modelo.RetrofitClient
import com.example.educafe.Modelo.clsRegistroSolicitud
import com.example.educafe.Modelo.clsRegistroRespuesta
import com.example.educafe.Modelo.clsValidacionesAcceso
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class clsRegistroPresentador(
    private val vista: VistaRegistro,
    private val alcance: CoroutineScope
) {

    // 1. Comunicación con la Activity.
    interface VistaRegistro {
        fun mostrarCarga(cargando: Boolean)
        fun mostrarError(mensaje: String)
        fun registroCorrecto(datos: clsRegistroRespuesta)
    }

    // 2. Validaciones compartidas.
    private val validaciones = clsValidacionesAcceso()
    private var registrando = false

    // 3. Registrar un cliente.
    fun registrarCliente(
        nombre: String,
        usuario: String,
        celular: String,
        password: String,
        confirmarPassword: String
    ) {

        // Evitar registros duplicados por varios clics.
        if (registrando) return

        val nombreLimpio = nombre.trim()
        val usuarioLimpio = usuario.trim().lowercase()
        val celularLimpio = celular.trim()

        // 4. Validar los campos.
        val error = validaciones.validarRegistro(
            nombreLimpio,
            usuarioLimpio,
            celularLimpio,
            password,
            confirmarPassword
        )

        if (error.isNotEmpty()) {
            vista.mostrarError(error)
            return
        }

        registrando = true
        vista.mostrarCarga(true)

        alcance.launch {
            try {

                // 5. Preparar los datos del cliente.
                val solicitud = clsRegistroSolicitud(
                    nombre = nombreLimpio,
                    usuario = usuarioLimpio,
                    celular = celularLimpio,
                    password = password,
                    confirmar_password = confirmarPassword
                )

                // 6. Enviar datos a la API.
                val respuesta =
                    RetrofitClient.apiService.registrarCliente(
                        solicitud
                    )

                // 7. Comprobar la respuesta.
                if (respuesta.status == "success" &&
                    respuesta.data != null
                ) {

                    vista.registroCorrecto(respuesta.data)

                } else {

                    vista.mostrarError(
                        respuesta.message.ifBlank {
                            "No se pudo crear la cuenta"
                        }
                    )
                }

            } catch (e: HttpException) {

                // 8. Leer errores del servidor.
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

            } catch (e: IOException) {

                vista.mostrarError(
                    "No hay conexión con el servidor. Revisa tu internet."
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                vista.mostrarError(
                    "Ocurrió un problema al crear la cuenta"
                )

            } finally {

                registrando = false
                vista.mostrarCarga(false)
            }
        }
    }
}
