package com.example.educafe.Modelo

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object clsApiError {

    private val json = Json { ignoreUnknownKeys = true }

    fun leer(e: Exception): dataErrorApi {
        if (e is HttpException) {
            val codigo = e.code()
            var mensaje = "Error del servidor ($codigo)"
            try {
                // errorBody solo se puede leer una vez
                val texto = e.response()?.errorBody()?.string() ?: ""
                val mensajeServidor = json.parseToJsonElement(texto).jsonObject["message"]?.jsonPrimitive?.content
                if (!mensajeServidor.isNullOrBlank()) {
                    mensaje = mensajeServidor
                }
            } catch (x: Exception) {

            }
            return dataErrorApi(codigo, mensaje, false)
        }
        if (e is SocketTimeoutException) {
            return dataErrorApi(0, "El servidor tardó en responder", true)
        }
        if (e is IOException) {
            return dataErrorApi(0, "Sin conexión. Revisa tu internet e intenta de nuevo", true)
        }
        return dataErrorApi(-1, "Ocurrió un error: ${e.message}", false)
    }
}
