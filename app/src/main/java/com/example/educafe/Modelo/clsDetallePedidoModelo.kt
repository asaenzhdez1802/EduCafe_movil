package com.example.educafe.Modelo

// Modelo MVP de K3: Detalle del pedido
class clsDetallePedidoModelo {

    // Consultar los datos reales de un pedido.
    // El interceptor de RetrofitClient agrega el Bearer Token.
    suspend fun obtenerDetallePedido(idPedido: Int): RespuestaApi<dataDetallePedido> {

        require(idPedido > 0) {
            "El identificador del pedido no es válido"
        }
        return RetrofitClient.apiService.obtenerDetallePedido(idPedido)
    }
}