
package com.example.educafe.interfaceApi

import com.example.educafe.Modelo.dataProductos
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
import com.example.educafe.Modelo.dataPedidos

import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {


    @GET("obtener_categorias.php")
    suspend fun obtenerCategorias():
            RespuestaApi<List<dataCategorias>>


    @POST("login.php")
    suspend fun iniciarSesion(
        @Body solicitud: clsLoginSolicitud
    ): RespuestaApi<clsUsuarioSesion>

    @POST("cerrar_sesion.php")
    suspend fun cerrarSesion(
        @Body solicitud: JsonObject
    ): RespuestaApi<JsonObject>


    @POST("registrar_cliente.php")
    suspend fun registrarCliente(
        @Body solicitud: clsRegistroSolicitud
    ): RespuestaApi<clsRegistroRespuesta>

    @GET("obtener_perfil.php")
    suspend fun obtenerPerfil():
            RespuestaApi<clsPerfilModelo>


    @GET("obtener_recompensas.php")
    suspend fun obtenerRecompensas():
            RespuestaApi<clsRecompensasModelo>

    // 7. Obtener información
    @GET("obtener_informacion.php")
    suspend fun obtenerInformacion():
            RespuestaApi<clsInformacionModelo>


    @POST("registrar_pedido.php")
    suspend fun registrarPedido(
        @Body solicitud: dataSolicitudPedido
    ): RespuestaApi<dataPedidoRegistrado>


    @GET("obtener_mis_pedidos.php")
    suspend fun obtenerMisPedidos(
        @Query("grupo") grupo: String
    ): RespuestaApi<List<dataPedidoCliente>>


    @GET("obtener_historial.php")
    suspend fun obtenerHistorial():
            RespuestaApi<List<dataHistorial>>


    @GET("obtener_pedidos.php")
    suspend fun obtenerPedidos(
        @Query("estado") estado: String? = null
    ): RespuestaApi<List<dataPedidos>>


    @GET("obtener_productos.php")
    suspend fun obtenerProductos(
        @Query("id_categoria") idCategoria: Int = 0
    ): RespuestaApi<List<dataProductos>>
}