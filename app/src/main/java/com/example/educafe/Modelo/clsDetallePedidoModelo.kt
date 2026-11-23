package com.example.educafe.Modelo

// Modelo MVP de K3: Detalle del pedido
class clsDetallePedidoModelo {

    // Consultar los datos reales de un pedido
    suspend fun obtenerDetallePedido(
        idPedido: Int
    ): RespuestaApi<dataDetallePedido> {

        require(idPedido > 0) {
            "El identificador del pedido no es válido"
        }

        return RetrofitClient.apiService
            .obtenerDetallePedido(idPedido)
    }

    // Marcar un pedido como listo mediante la API
    suspend fun marcarPedidoListo(
        idPedido: Int
    ): RespuestaApi<dataPedidoActualizado> {

        require(idPedido > 0) {
            "El identificador del pedido no es válido"
        }

        val solicitud = dataActualizarPedido(
            id_pedido = idPedido,
            accion = "listo"
        )

        return RetrofitClient.apiService
            .actualizarPedido(solicitud)
    }
}