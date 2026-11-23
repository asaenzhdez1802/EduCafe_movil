package com.example.educafe.Presentador

import com.example.educafe.Modelo.clsDetallePedidoModelo
import com.example.educafe.Modelo.dataDetallePedido
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

interface VistaDetallePedido {

    fun mostrarCarga()

    fun mostrarDetallePedido(pedido: dataDetallePedido)

    fun mostrarError(mensaje: String)

    fun solicitarLogin()

    fun mostrarActualizando()

    fun mostrarActualizacionCorrecta(mensaje: String)

    fun habilitarBotonListo()
}

class clsDetallePedidoPresentador(
    private val vista: VistaDetallePedido,
    private val scope: CoroutineScope
) {

    private val modelo = clsDetallePedidoModelo()
    private var actualizando = false

    // Consultar detalle_pedido.php
    fun cargarDetallePedido(idPedido: Int) {

        if (idPedido <= 0) {
            vista.mostrarError("El pedido no es válido")
            return
        }

        vista.mostrarCarga()

        scope.launch {
            try {

                val respuesta =
                    modelo.obtenerDetallePedido(idPedido)

                if (respuesta.status == "success") {

                    val pedido = respuesta.data

                    if (pedido != null) {
                        vista.mostrarDetallePedido(pedido)
                    } else {
                        vista.mostrarError(
                            "No se recibió el detalle del pedido"
                        )
                    }

                } else {
                    vista.mostrarError(respuesta.message)
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: HttpException) {

                when (e.code()) {
                    401 -> vista.solicitarLogin()
                    403 -> vista.mostrarError(
                        "No tienes permiso para consultar el pedido"
                    )
                    else -> vista.mostrarError(
                        "Error del servidor: ${e.code()}"
                    )
                }

            } catch (e: IOException) {
                vista.mostrarError(
                    "No se pudo conectar con el servidor"
                )

            } catch (e: Exception) {
                vista.mostrarError(
                    "No se pudo consultar el pedido"
                )
            }
        }
    }

    // POST actualizar_pedido.php
    // Envía id_pedido y accion = "listo"
    fun marcarPedidoListo(idPedido: Int) {

        if (idPedido <= 0) {
            vista.mostrarError("El pedido no es válido")
            return
        }

        // Evitar solicitudes repetidas
        if (actualizando) return

        actualizando = true
        vista.mostrarActualizando()

        scope.launch {
            try {

                val respuesta =
                    modelo.marcarPedidoListo(idPedido)

                if (respuesta.status == "success") {

                    vista.mostrarActualizacionCorrecta(
                        respuesta.message.ifBlank {
                            "Pedido marcado como listo"
                        }
                    )

                    // Consultar nuevamente el pedido actualizado
                    val detalle =
                        modelo.obtenerDetallePedido(idPedido)

                    if (detalle.status == "success" &&
                        detalle.data != null
                    ) {
                        vista.mostrarDetallePedido(detalle.data)
                    } else {
                        vista.mostrarError(
                            "El pedido se actualizó, pero no se pudo recargar"
                        )
                    }

                } else {
                    vista.mostrarError(respuesta.message)
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: HttpException) {

                when (e.code()) {
                    401 -> vista.solicitarLogin()
                    403 -> vista.mostrarError(
                        "No tienes permiso para actualizar el pedido"
                    )
                    else -> vista.mostrarError(
                        "No se pudo actualizar: ${e.code()}"
                    )
                }

            } catch (e: IOException) {
                vista.mostrarError(
                    "No hay conexión con el servidor"
                )

            } catch (e: Exception) {
                vista.mostrarError(
                    "No se pudo actualizar el pedido"
                )

            } finally {
                actualizando = false
                vista.habilitarBotonListo()
            }
        }
    }
}