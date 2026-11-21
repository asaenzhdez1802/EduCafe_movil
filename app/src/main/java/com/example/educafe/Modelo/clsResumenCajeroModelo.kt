package com.example.educafe.Modelo

// Modelo de la pantalla Resumen del cajero
class clsResumenCajeroModelo {

    private val pedidosModelo = clsPedidosModelo()

    // Consultar los pedidos del sistema
    suspend fun consultarPedidos(estado: String? = null): RespuestaApi<List<dataPedidos>> {
        return pedidosModelo.obtenerPedidos(estado)
    }
}