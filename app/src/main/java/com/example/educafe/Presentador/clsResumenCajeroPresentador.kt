package com.example.educafe.Presentador

import com.example.educafe.Modelo.clsResumenCajeroModelo
import com.example.educafe.Modelo.dataPedidos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

interface VistaResumenCajero {

    fun mostrarCarga()
    fun mostrarPedidos(pedidos: List<dataPedidos>)
    fun mostrarError(mensaje: String)
    fun solicitarLogin()
}

// Presentador de  Resumen del cajero
class clsResumenCajeroPresentador(private val vista: VistaResumenCajero, private val scope: CoroutineScope) {

    private val modelo = clsResumenCajeroModelo()

    // Solicita los pedidos para el resumen
    fun cargarResumen() {

        vista.mostrarCarga()

        scope.launch {
            try {
                val respuesta = modelo.consultarPedidos()

                if (respuesta.status == "success") {

                    val pedidos = respuesta.data

                    if (pedidos != null) {
                        vista.mostrarPedidos(pedidos)
                    } else {
                        vista.mostrarError("No se recibieron datos de pedidos")
                    }

                } else {
                    vista.mostrarError(respuesta.message)
                }

            } catch (e: HttpException) {

                when (e.code()) {
                    401 -> vista.solicitarLogin()
                    403 -> vista.mostrarError("No tienes permiso para consultar pedidos")
                    else -> vista.mostrarError("Error del servidor: ${e.code()}")
                }
            } catch (e: Exception) {
                vista.mostrarError("No se pudo consultar el resumen")
            }
        }
    }
}