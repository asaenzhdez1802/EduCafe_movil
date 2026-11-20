package com.example.educafe.Modelo


class clsPedidosModelo {

    // Sin token: el interceptor de RetrofitClient pone "Authorization: Bearer ..."
    // C3 · POST registrar_pedido.php
    suspend fun registrarPedido(solicitud: dataSolicitudPedido): RespuestaApi<dataPedidoRegistrado> {
        return RetrofitClient.apiService.registrarPedido(solicitud)
    }

    // C5 · GET obtener_mis_pedidos.php?grupo=activos
    suspend fun obtenerPedidosActivos(): RespuestaApi<List<dataPedidoCliente>> {
        return RetrofitClient.apiService.obtenerMisPedidos("activos")
    }

    // C6 · GET obtener_historial.php
    suspend fun obtenerHistorial(): RespuestaApi<List<dataHistorial>> {
        return RetrofitClient.apiService.obtenerHistorial()
    }
}
