package com.example.educafe.Modelo

// Modelo de la pantalla K1 - Resumen del cajero
class clsResumenCajeroModelo {

    // Reutilizamos el modelo de pedidos existente
    private val pedidosModelo = clsPedidosModelo()

    // Consultar los pedidos del sistema
    suspend fun consultarPedidos(
        estado: String? = null
    ): RespuestaApi<List<dataPedidos>> {

        return pedidosModelo.obtenerPedidos(estado)
    }
}