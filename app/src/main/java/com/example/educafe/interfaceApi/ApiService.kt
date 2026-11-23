package com.example.educafe.interfaceApi

import com.example.educafe.Modelo.dataResumenAdmin
import com.example.educafe.Modelo.dataMasVendidos
import com.example.educafe.Modelo.dataProductos
import com.example.educafe.Modelo.RespuestaApi
import com.example.educafe.Modelo.clsLoginSolicitud
import com.example.educafe.Modelo.clsUsuarioSesion
import com.example.educafe.Modelo.dataCategorias
import com.example.educafe.Modelo.dataHistorial
import com.example.educafe.Modelo.dataPedidoCliente
import com.example.educafe.Modelo.dataPedidoRegistrado
import com.example.educafe.Modelo.dataSolicitudPedido
import com.example.educafe.Modelo.dataPedidos
import com.example.educafe.Modelo.dataDetallePedido
import com.example.educafe.Modelo.dataActualizarPedido
import com.example.educafe.Modelo.dataPedidoActualizado
import com.example.educafe.Modelo.clsRegistroSolicitud
import com.example.educafe.Modelo.clsRegistroRespuesta
import com.example.educafe.Modelo.clsPerfilModelo
import com.example.educafe.Modelo.clsRecompensasModelo
import com.example.educafe.Modelo.dataResumenCajero
import com.example.educafe.Modelo.clsInformacionModelo
import com.example.educafe.Modelo.dataMisVentasCajero
import com.example.educafe.Modelo.dataStockAdmin
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // 1. Consultar categorías
    @GET("obtener_categorias.php")
    suspend fun obtenerCategorias():
            RespuestaApi<List<dataCategorias>>

    // 2. Iniciar sesión
    @POST("login.php")
    suspend fun iniciarSesion(
        @Body solicitud: clsLoginSolicitud
    ): RespuestaApi<clsUsuarioSesion>

    // 3. Cerrar sesión
    @POST("cerrar_sesion.php")
    suspend fun cerrarSesion(
        @Body solicitud: JsonObject
    ): RespuestaApi<JsonObject>

    // Registrar pedido del cliente
    @POST("registrar_pedido.php")
    suspend fun registrarPedido(
        @Body solicitud: dataSolicitudPedido
    ): RespuestaApi<dataPedidoRegistrado>

    // Consultar pedidos del cliente
    @GET("obtener_mis_pedidos.php")
    suspend fun obtenerMisPedidos(
        @Query("grupo") grupo: String
    ): RespuestaApi<List<dataPedidoCliente>>

    // Consultar historial
    @GET("obtener_historial.php")
    suspend fun obtenerHistorial():
            RespuestaApi<List<dataHistorial>>

    // K2: Consultar pedidos del cajero
    @GET("obtener_pedidos.php")
    suspend fun obtenerPedidos(
        @Query("estado") estado: String? = null
    ): RespuestaApi<List<dataPedidos>>

    // K3: Consultar detalle de un pedido
    @GET("detalle_pedido.php")
    suspend fun obtenerDetallePedido(
        @Query("id_pedido") idPedido: Int
    ): RespuestaApi<dataDetallePedido>

    // Consultar productos
    @GET("obtener_productos.php")
    suspend fun obtenerProductos(
        @Query("id_categoria") idCategoria: Int = 0
    ): RespuestaApi<List<dataProductos>>

    // Ventas generales del administrador
    @GET("obtener_resumen_admin.php")
    suspend fun obtenerResumenAdmin(
        @Query("periodo") periodo: String
    ): RespuestaApi<dataResumenAdmin>

    // Productos más vendidos
    @GET("obtener_mas_vendidos.php")
    suspend fun obtenerMasVendidos(
        @Query("periodo") periodo: String
    ): RespuestaApi<dataMasVendidos>

    // Registrar cliente
    @POST("registrar_cliente.php")
    suspend fun registrarCliente(
        @Body solicitud: clsRegistroSolicitud
    ): RespuestaApi<clsRegistroRespuesta>

    @GET("obtener_resumen_cajero.php")
    suspend fun obtenerResumenCajero():
            RespuestaApi<dataResumenCajero>

    // Consultar perfil
    @GET("obtener_perfil.php")
    suspend fun obtenerPerfil():
            RespuestaApi<clsPerfilModelo>

    // Consultar recompensas
    @GET("obtener_recompensas.php")
    suspend fun obtenerRecompensas():
            RespuestaApi<clsRecompensasModelo>

    // Consultar información
    @GET("obtener_info.php")
    suspend fun obtenerInformacion():
            RespuestaApi<clsInformacionModelo>

    @POST("actualizar_pedido.php")
    suspend fun actualizarPedido(
        @Body solicitud: dataActualizarPedido
    ): RespuestaApi<dataPedidoActualizado>

    @GET("obtener_stock.php")
    suspend fun obtenerStock():
            RespuestaApi<List<dataStockAdmin>>

    @GET("obtener_mis_ventas.php")
    suspend fun obtenerMisVentas(
        @Query("fecha") fecha: String? = null
    ): RespuestaApi<dataMisVentasCajero>
}