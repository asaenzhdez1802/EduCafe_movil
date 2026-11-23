package com.example.educafe.Modelo

class clsMisVentasCajeroModelo {

    // K4: Consultar las ventas del cajero
    suspend fun consultarMisVentas(
        fecha: String? = null
    ): RespuestaApi<dataMisVentasCajero> {

        return RetrofitClient.apiService.obtenerMisVentas(fecha)
    }
}