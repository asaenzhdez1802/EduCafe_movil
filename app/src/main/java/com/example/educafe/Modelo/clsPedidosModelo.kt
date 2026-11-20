package com.example.educafe.Modelo


class clsPedidosModelo {

    suspend fun registrarPedido(solicitud: dataSolicitudPedido): RespuestaApi<dataPedidoRegistrado> {
        return RetrofitClient.apiService.registrarPedido(solicitud)
    }


}
