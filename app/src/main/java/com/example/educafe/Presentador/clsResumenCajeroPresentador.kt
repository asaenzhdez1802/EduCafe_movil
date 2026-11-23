package com.example.educafe.Presentador

import com.example.educafe.Modelo.clsResumenCajeroModelo
import com.example.educafe.Modelo.dataPedidos
import com.example.educafe.Modelo.dataResumenCajero
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

// Comunicación entre el Presentador y K1
interface VistaResumenCajero {

    fun mostrarCarga()

    fun mostrarPedidos(pedidos: List<dataPedidos>)

    // Datos reales de ventas del cajero
    // Se conectará con las tarjetas de la Activity
    fun mostrarEstadisticas(datos: dataResumenCajero) {}

    fun mostrarError(mensaje: String)

    fun solicitarLogin()
}

// Presentador de K1: Resumen del cajero
class clsResumenCajeroPresentador(
    private val vista: VistaResumenCajero,
    private val scope: CoroutineScope
) {

    private val modelo = clsResumenCajeroModelo()

    fun cargarResumen() {

        vista.mostrarCarga()

        scope.launch {

            // Primera consulta: pedidos generales
            try {

                val respuesta = modelo.consultarPedidos()

                if (respuesta.status == "success") {

                    vista.mostrarPedidos(
                        respuesta.data ?: emptyList()
                    )

                } else {

                    vista.mostrarError(respuesta.message)
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: HttpException) {

                if (e.code() == 401) {
                    vista.solicitarLogin()
                    return@launch
                }

                vista.mostrarError(
                    "Error al consultar pedidos: ${e.code()}"
                )

            } catch (e: Exception) {

                vista.mostrarError(
                    "No se pudieron consultar los pedidos"
                )
            }

            // Segunda consulta: ventas reales del cajero
            try {

                val respuesta = modelo.consultarEstadisticas()

                if (respuesta.status == "success") {

                    val datos = respuesta.data

                    if (datos != null) {
                        vista.mostrarEstadisticas(datos)
                    } else {
                        vista.mostrarError(
                            "No se recibieron estadísticas del cajero"
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
                        "No tienes permiso para consultar el resumen"
                    )

                    else -> vista.mostrarError(
                        "Error al consultar ventas: ${e.code()}"
                    )
                }

            } catch (e: Exception) {

                vista.mostrarError(
                    "No se pudieron consultar las estadísticas"
                )
            }
        }
    }
}