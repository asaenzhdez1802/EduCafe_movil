package com.example.educafe.Presentador

import com.example.educafe.Modelo.clsMisVentasCajeroModelo
import com.example.educafe.Modelo.dataMisVentasCajero
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

// Comunicación entre el Presentador y la pantalla K4
interface VistaMisVentasCajero {

    fun mostrarCarga()

    fun mostrarVentas(datos: dataMisVentasCajero)

    fun mostrarError(mensaje: String)

    fun solicitarLogin()
}

// Presentador de K4: Mis ventas del cajero
class clsMisVentasCajeroPresentador(
    private val vista: VistaMisVentasCajero,
    private val scope: CoroutineScope
) {

    private val modelo = clsMisVentasCajeroModelo()

    // Consultar ventas de hoy o de una fecha seleccionada
    fun cargarVentas(fecha: String? = null) {

        vista.mostrarCarga()

        scope.launch {
            try {

                val respuesta = modelo.consultarMisVentas(fecha)

                if (respuesta.status == "success") {

                    val datos = respuesta.data

                    if (datos != null) {
                        vista.mostrarVentas(datos)
                    } else {
                        vista.mostrarError(
                            "No se recibieron datos de ventas"
                        )
                    }

                } else {
                    vista.mostrarError(respuesta.message)
                }

            } catch (e: HttpException) {

                when (e.code()) {

                    401 -> vista.solicitarLogin()

                    403 -> vista.mostrarError(
                        "No tienes permiso para consultar las ventas"
                    )

                    else -> vista.mostrarError(
                        "Error del servidor: ${e.code()}"
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                vista.mostrarError(
                    "No se pudieron consultar las ventas: " +
                            (e.localizedMessage ?: "Error de conexión")
                )
            }
        }
    }
}