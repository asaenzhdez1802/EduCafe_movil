
package com.example.educafe.interfaceApi

import com.example.educafe.Modelo.RespuestaApi
import com.example.educafe.Modelo.clsLoginSolicitud
import com.example.educafe.Modelo.clsUsuarioSesion
import com.example.educafe.Modelo.clsRegistroSolicitud
import com.example.educafe.Modelo.clsRegistroRespuesta
import com.example.educafe.Modelo.clsPerfilModelo
import com.example.educafe.Modelo.clsRecompensasModelo
import com.example.educafe.Modelo.clsInformacionModelo
import com.example.educafe.Modelo.dataCategorias
import com.example.educafe.Modelo.dataHistorial
import com.example.educafe.Modelo.dataPedidoCliente
import com.example.educafe.Modelo.dataPedidoRegistrado
import com.example.educafe.Modelo.dataSolicitudPedido
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

    // 3. Cerrar sesión.
    @POST("cerrar_sesion.php")
    suspend fun cerrarSesion(
        @Body solicitud: JsonObject
    ): RespuestaApi<JsonObject>

    // 4. Registrar un cliente.
    @POST("registrar_cliente.php")
    suspend fun registrarCliente(
        @Body solicitud: clsRegistroSolicitud
    ): RespuestaApi<clsRegistroRespuesta>

    // 5. Consultar perfil.
    @GET("obtener_perfil.php")
    suspend fun obtenerPerfil():
            RespuestaApi<clsPerfilModelo>

    // 6. Consultar recompensas.
    @GET("obtener_recompensas.php")
    suspend fun obtenerRecompensas():
            RespuestaApi<clsRecompensasModelo>

    // 7. Consultar información de la cafetería.
    @GET("obtener_informacion.php")
    suspend fun obtenerInformacion():
            RespuestaApi<clsInformacionModelo>

    // 8. Registrar pedidos del cliente.
    @POST("registrar_pedido.php")
    suspend fun registrarPedido(
        @Body solicitud: dataSolicitudPedido
    ): RespuestaApi<dataPedidoRegistrado>

    // 9. Consultar pedidos del cliente.
    @GET("obtener_mis_pedidos.php")
    suspend fun obtenerMisPedidos(
        @Query("grupo") grupo: String
    ): RespuestaApi<List<dataPedidoCliente>>

    // 10. Consultar historial.
    @GET("obtener_historial.php")
    suspend fun obtenerHistorial():
            RespuestaApi<List<dataHistorial>>
}