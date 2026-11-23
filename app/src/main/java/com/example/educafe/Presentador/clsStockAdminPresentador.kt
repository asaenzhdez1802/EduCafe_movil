package com.example.educafe.Presentador

import com.example.educafe.Modelo.clsStockAdminModelo
import com.example.educafe.Modelo.dataStockAdmin
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

interface VistaStockAdmin {

    fun mostrarCarga()

    fun mostrarStock(productos: List<dataStockAdmin>)

    fun mostrarError(mensaje: String)

    fun solicitarLogin()
}

class clsStockAdminPresentador(
    private val vista: VistaStockAdmin,
    private val scope: CoroutineScope
) {

    private val modelo = clsStockAdminModelo()

    fun cargarStock() {

        vista.mostrarCarga()

        scope.launch {
            try {

                val respuesta = modelo.consultarStock()

                if (respuesta.status == "success" &&
                    respuesta.data != null
                ) {
                    vista.mostrarStock(respuesta.data)
                } else {
                    vista.mostrarError(respuesta.message)
                }

            } catch (e: HttpException) {

                when (e.code()) {

                    401 -> vista.solicitarLogin()

                    403 -> vista.mostrarError(
                        "Acceso exclusivo para administradores"
                    )

                    else -> vista.mostrarError(
                        "Error del servidor: ${e.code()}"
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                vista.mostrarError(
                    "No se pudo consultar el inventario: " +
                            (e.localizedMessage ?: "Error de conexión")
                )
            }
        }
    }
}