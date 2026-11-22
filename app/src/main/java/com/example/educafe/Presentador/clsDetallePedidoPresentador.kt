package com.example.educafe.Presentador

import com.example.educafe.Modelo.clsDetallePedidoModelo
import com.example.educafe.Modelo.dataDetallePedido
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// Interfaz que comunica el Presentador con K3
interface VistaDetallePedido {

    // Mostrar que se está consultando el pedido
    fun mostrarCarga()

    // Mostrar la información real del pedido
    fun mostrarDetallePedido(pedido: dataDetallePedido)

    // Mostrar los errores
    fun mostrarError(mensaje: String)

    // Avisar cuando la sesión haya vencido
    fun solicitarLogin()
}

// Presentador Detalle del pedido
class clsDetallePedidoPresentador(private val vista: VistaDetallePedido, private val scope: CoroutineScope) {
    // Modelo encargado de comunicarse con Retrofit
    private val modelo = clsDetallePedidoModelo()


    fun cargarDetallePedido(idPedido: Int) {

        if (idPedido <= 0) {
            vista.mostrarError("El ID del pedido no es válido")
            return
        }

        // Avisar que comienza la consulta
        vista.mostrarCarga()

        scope.launch {
            try {
                // Consultar detalle_pedido.php
                val respuesta = modelo.obtenerDetallePedido(idPedido)

                if (respuesta.status == "success") {

                    val pedido = respuesta.data

                    if (pedido != null) {
                        // Enviar el pedido a la Vista
                        vista.mostrarDetallePedido(pedido)
                    } else {
                        vista.mostrarError("No se recibió el detalle del pedido")
                    }
                } else {
                    vista.mostrarError(respuesta.message)
                }
            } catch (e: CancellationException) {
                // Respetar la cancelación del ciclo de vida
                throw e
            } catch (e: HttpException) {
                // Manejar los errores HTTP del servidor
                when (e.code()) {
                    400 -> vista.mostrarError("Pedido no encontrado o solicitud inválida")
                    401 -> vista.solicitarLogin()
                    403 -> vista.mostrarError("No tienes permiso para consultar este pedido")
                    else -> vista.mostrarError("Error del servidor: ${e.code()}")
                }

            } catch (e: IOException) {
                vista.mostrarError("No se pudo conectar con el servidor")
            } catch (e: Exception) {
                vista.mostrarError("No se pudo obtener el detalle del pedido")
            }
        }
    }
}