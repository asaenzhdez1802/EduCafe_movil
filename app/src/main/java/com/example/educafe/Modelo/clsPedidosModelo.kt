package com.example.educafe.Modelo


class clsPedidosModelo {


    suspend fun registrarPedido(solicitud: dataSolicitudPedido): RespuestaApi<dataPedidoRegistrado> {
        return RetrofitClient.apiService.registrarPedido(solicitud)
    }

    // C5 · GET obtener_mis_pedidos.php?grupo=activos
    suspend fun obtenerPedidosActivos(): RespuestaApi<List<dataPedidoCliente>> {
        return RetrofitClient.apiService.obtenerMisPedidos("activos")
    }


}
