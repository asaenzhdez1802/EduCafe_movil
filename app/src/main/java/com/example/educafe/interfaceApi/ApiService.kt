package com.example.educafe.interfaceApi

import com.example.educafe.Modelo.RespuestaApi
import com.example.educafe.Modelo.clsLoginSolicitud
import com.example.educafe.Modelo.clsUsuarioSesion
import com.example.educafe.Modelo.dataCategorias
import com.example.educafe.Modelo.dataPedidos
import retrofit2.http.Query
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {

    // 1. Consultar las categorías del menú.
    @GET("obtener_categorias.php")
    suspend fun obtenerCategorias():
            RespuestaApi<List<dataCategorias>>

    // 2. Iniciar sesión.
    @POST("login.php")
    suspend fun iniciarSesion(
        @Body solicitud: clsLoginSolicitud
    ): RespuestaApi<clsUsuarioSesion>

    // 3. Revocar la sesión actual en el servidor.
    @POST("cerrar_sesion.php")
    suspend fun cerrarSesion(
        @Body solicitud: JsonObject
    ): RespuestaApi<JsonObject>

    // 4. Consultar los pedidos del personal
    @GET("obtener_pedidos.php")
    suspend fun obtenerPedidos(
        @Query("estado") estado: String? = null
    ): RespuestaApi<List<dataPedidos>>
}