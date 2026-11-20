package com.example.educafe.interfaceApi

import com.example.educafe.Modelo.RespuestaApi
import com.example.educafe.Modelo.clsLoginSolicitud
import com.example.educafe.Modelo.clsUsuarioSesion
import com.example.educafe.Modelo.clsRegistroSolicitud
import com.example.educafe.Modelo.clsRegistroRespuesta
import com.example.educafe.Modelo.clsPerfilModelo
import com.example.educafe.Modelo.clsRecompensasModelo
import com.example.educafe.Modelo.dataCategorias
import kotlinx.serialization.json.JsonObject
import com.example.educafe.Modelo.clsInformacionModelo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // 1. Consultar categorías del menú.
    @GET("obtener_categorias.php")
    suspend fun obtenerCategorias():
            RespuestaApi<List<dataCategorias>>

    // 2. Iniciar sesión.
    @POST("login.php")
    suspend fun iniciarSesion(
        @Body solicitud: clsLoginSolicitud
    ): RespuestaApi<clsUsuarioSesion>

    // 3. Cerrar sesión.
    @POST("cerrar_sesion.php")
    suspend fun cerrarSesion(
        @Body solicitud: JsonObject
    ): RespuestaApi<JsonObject>

    // 4. Registrar clientes.
    @POST("registrar_cliente.php")
    suspend fun registrarCliente(
        @Body solicitud: clsRegistroSolicitud
    ): RespuestaApi<clsRegistroRespuesta>

    // 5. Consultar perfil del cliente.
    @GET("obtener_perfil.php")
    suspend fun obtenerPerfil():
            RespuestaApi<clsPerfilModelo>

    // 6. Consultar puntos y recompensas.
    @GET("obtener_recompensas.php")
    suspend fun obtenerRecompensas():
            RespuestaApi<clsRecompensasModelo>


    // 7. Consultar información de Edu Café.
    @GET("obtener_informacion.php")
    suspend fun obtenerInformacion():
            RespuestaApi<clsInformacionModelo>
}
