package com.example.educafe.Modelo

class clsStockAdminModelo {

    suspend fun consultarStock():
            RespuestaApi<List<dataStockAdmin>> {

        return RetrofitClient.apiService.obtenerStock()
    }
}