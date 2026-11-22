package com.example.educafe.Presentador

import com.example.educafe.Modelo.RetrofitClient
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.clsLoginSolicitud
import com.example.educafe.Modelo.clsValidacionesAcceso
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import org.json.JSONObject

class clsLoginPresentador(
    private val vista: VistaLogin,
    private val alcance: CoroutineScope
) {

    interface VistaLogin {
        fun mostrarCarga(cargando: Boolean)
        fun mostrarError(mensaje: String)
        fun loginCorrecto(destino: NavegacionSesion.Destino)
    }

    private val validaciones = clsValidacionesAcceso()
    private var enviando = false

    fun iniciarSesion(usuario: String, password: String) {

        // 1. Evitar solicitudes repetidas.
        if (enviando) return

        // 2. Validar los campos.
        val error = validaciones.validarLogin(usuario, password)

        if (error.isNotEmpty()) {
            vista.mostrarError(error)
            return
        }

        enviando = true
        vista.mostrarCarga(true)

        // 3. Consultar login.php.
        alcance.launch {
            try {
                val respuesta = RetrofitClient.apiService.iniciarSesion(
                    clsLoginSolicitud(usuario.trim(), password)
                )

                if (respuesta.status == "success" &&
                    respuesta.data != null
                ) {
                    // 4. Guardar el token y los datos.
                    SesionUsuario.guardarSesion(respuesta.data)

                    // 5. Identificar destino según el rol.
                    val destino =
                        NavegacionSesion.destinoDespuesDelLogin()

                    vista.loginCorrecto(destino)

                } else {
                    vista.mostrarError(
                        respuesta.message.ifBlank {
                            "No se pudo iniciar sesión"
                        }
                    )
                }

            } catch (e: HttpException) {

                // 6. Leer el mensaje de error del servidor.
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

                val mensaje = when (e.code()) {
                    401 -> "Usuario o contraseña incorrectos"
                    403 -> "No tienes permiso para acceder"
                    else -> mensajeServidor.ifBlank {
                        "Error del servidor (${e.code()})"
                    }
                }

                vista.mostrarError(mensaje)

            } catch (e: IOException) {
                vista.mostrarError(
                    "No se pudo conectar con el servidor. Revisa tu internet."
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                vista.mostrarError(
                    "Ocurrió un problema al iniciar sesión"
                )

            } finally {
                enviando = false
                vista.mostrarCarga(false)
            }
        }
    }
}
