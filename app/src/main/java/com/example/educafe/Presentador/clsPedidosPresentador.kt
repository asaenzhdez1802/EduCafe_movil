package com.example.educafe.Presentador

import com.example.educafe.Modelo.clsPedidosModelo
import com.example.educafe.Modelo.dataPedidos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

// Comunicación entre el Presentador y la Vista
interface VistaPedidos {
    fun mostrarCarga()
    fun mostrarPedidos(pedidos: List<dataPedidos>)
    fun mostrarError(mensaje: String)
    fun solicitarLogin()
}

// Presentador para consultar los pedidos
class clsPedidosPresentador(
    private val vista: VistaPedidos
) {

    private val modelo = clsPedidosModelo()

    fun cargarPedidos(estado: String? = null) {

        vista.mostrarCarga()

        CoroutineScope(Dispatchers.Main).launch {

            try {
                val respuesta = modelo.obtenerPedidos(estado)

                if (respuesta.status == "success") {
                    vista.mostrarPedidos(respuesta.data ?: emptyList())
                } else {
                    vista.mostrarError(respuesta.message)
                }

            } catch (error: HttpException) {

                if (error.code() == 401) {
                    vista.solicitarLogin()
                } else if (error.code() == 403) {
                    vista.mostrarError("No tienes permiso")
                } else {
                    vista.mostrarError("Error al consultar pedidos")
                }

            } catch (error: Exception) {
                vista.mostrarError("Error de conexión")
            }
        }
    }
}