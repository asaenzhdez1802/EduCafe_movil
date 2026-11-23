package com.example.educafe.Modelo

// Modelo de K1: Resumen del cajero
class clsResumenCajeroModelo {

    private val pedidosModelo = clsPedidosModelo()

    // Consultar los pedidos generales del sistema
    suspend fun consultarPedidos(
        estado: String? = null
    ): RespuestaApi<List<dataPedidos>> {

        return pedidosModelo.obtenerPedidos(estado)
    }

    // Consultar las estadísticas reales del cajero
    suspend fun consultarEstadisticas():
            RespuestaApi<dataResumenCajero> {

        return RetrofitClient.apiService.obtenerResumenCajero()
    }
}