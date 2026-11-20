package com.example.educafe.Modelo

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SesionUsuario {

    // 1. Archivo privado donde se guardan los datos de sesión.
    private lateinit var preferencias: SharedPreferences

    private const val ARCHIVO = "educafe_sesion"
    private const val CLAVE_SESION = "datos_sesion"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    // 2. Preparar el almacenamiento al iniciar la aplicación.
    fun inicializar(context: Context) {
        preferencias = context.applicationContext.getSharedPreferences(
            ARCHIVO,
            Context.MODE_PRIVATE
        )
    }

    // 3. Guardar la respuesta de un login correcto.
    @Synchronized
    fun guardarSesion(datos: clsUsuarioSesion) {

        require(datos.id_usuario > 0) {
            "El usuario recibido no es válido"
        }

        require(datos.token.isNotBlank()) {
            "El servidor no devolvió un token"
        }

        require(datos.rol in setOf("cliente", "cajero", "admin")) {
            "El rol recibido no es válido"
        }

        val texto = json.encodeToString(datos)

        preferencias.edit()
            .putString(CLAVE_SESION, texto)
            .apply()
    }

    // 4. Recuperar los datos guardados.
    @Synchronized
    fun obtenerSesion(): clsUsuarioSesion? {

        val texto = preferencias.getString(CLAVE_SESION, null)
            ?: return null

        return try {
            json.decodeFromString<clsUsuarioSesion>(texto)
        } catch (e: Exception) {
            limpiarSesion()
            null
        }
    }

    // 5. Comprobar si existen credenciales guardadas.
    fun haySesion(): Boolean {

        val datos = obtenerSesion() ?: return false

        return datos.id_usuario > 0 &&
                datos.token.isNotBlank() &&
                datos.rol in setOf("cliente", "cajero", "admin")
    }

    // 6. Obtener el token para las peticiones.
    fun obtenerToken(): String? {
        return obtenerSesion()?.token
    }

    // 7. Obtener el rol para la navegación.
    fun obtenerRol(): String? {
        return obtenerSesion()?.rol
    }

    // 8. Eliminar los datos locales de sesión.
    @Synchronized
    fun limpiarSesion() {
        preferencias.edit()
            .remove(CLAVE_SESION)
            .apply()
    }

    // 9. Evitar que una petición antigua borre una sesión nueva.
    @Synchronized
    fun limpiarSiCoincide(tokenEnviado: String) {
        if (obtenerToken() == tokenEnviado) {
            limpiarSesion()
        }
    }
}