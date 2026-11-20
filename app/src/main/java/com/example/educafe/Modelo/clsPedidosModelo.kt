package com.example.educafe.Modelo

// Modelo para consultar los pedidos de la app
class clsPedidosModelo {

    // Obtener los pedidos desde la API
    suspend fun obtenerPedidos(
        estado: String? = null
    ): RespuestaApi<List<dataPedidos>> {

        return RetrofitClient.apiService.obtenerPedidos(estado)
    }
}