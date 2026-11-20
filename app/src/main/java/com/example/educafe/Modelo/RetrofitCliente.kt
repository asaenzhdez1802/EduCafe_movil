package com.example.educafe.Modelo

import com.example.educafe.interfaceApi.ApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitClient {

    // 1. Dirección de las API en el hosting.
    private const val BASE_URL =
        "https://manuelsh.somos4a.org/educafe_api/"

    // 2. Configuración para leer las respuestas JSON.
    private val json = Json {
        ignoreUnknownKeys = true
    }

    // 3. Cliente HTTP compartido.
    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor { cadena ->

            // Leer el token actual en cada petición.
            val token = SesionUsuario.obtenerToken()

            val constructor = cadena.request()
                .newBuilder()
                .header("Accept", "application/json")

            // Agregarlo solamente cuando exista una sesión.
            if (!token.isNullOrBlank()) {
                constructor.header(
                    "Authorization",
                    "Bearer $token"
                )
            }

            val respuesta = cadena.proceed(
                constructor.build()
            )

            // 4. El servidor indica que el token ya no es válido.
            if (respuesta.code == 401 && !token.isNullOrBlank()) {
                SesionUsuario.limpiarSiCoincide(token)
            }

            respuesta
        }
        .build()

    // 5. Crear Retrofit usando el cliente compartido.
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(clienteHttp)
        .addConverterFactory(
            json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
        .build()

    // 6. Servicio utilizado por todos los módulos.
    val apiService: ApiService =
        retrofit.create(ApiService::class.java)
}