package com.example.educafe.Modelo
import kotlinx.coroutines.CancellationException

class clsPedidosModelo {


    suspend fun registrarPedido(solicitud: dataSolicitudPedido): RespuestaApi<dataPedidoRegistrado> {
        return RetrofitClient.apiService.registrarPedido(solicitud)
    }


    suspend fun obtenerPedidosActivos(): RespuestaApi<List<dataPedidoCliente>> {
        return RetrofitClient.apiService.obtenerMisPedidos("activos")
    }

    suspend fun obtenerHistorialConPagados(): RespuestaApi<List<dataHistorial>> {
        val historial = obtenerHistorial()
        if (historial.status != "success") return historial

        val pagados = try {
            obtenerPedidosActivos().data.orEmpty()
                .filter { it.estadoPago == "pagado" }
                .map {
                    dataHistorial(
                        idPedido = it.idPedido,
                        folio = it.folio,
                        codigoRecogida = it.codigoRecogida,
                        fecha = it.fecha,
                        total = it.total,
                        estado = "entregado",
                        folioVenta = it.folioVenta,
                        origen = "app",
                        estadoPago = "pagado"
                    )
                }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emptyList()
        }

        val todo = (pagados + historial.data.orEmpty()).sortedByDescending { it.fecha }
        return historial.copy(data = todo)
    }


    suspend fun obtenerHistorial(): RespuestaApi<List<dataHistorial>> {
        return RetrofitClient.apiService.obtenerHistorial()
    }
    

    suspend fun obtenerPedidos(estado: String? = null): RespuestaApi<List<dataPedidos>> {
        return RetrofitClient.apiService.obtenerPedidos(estado)
    }
}
