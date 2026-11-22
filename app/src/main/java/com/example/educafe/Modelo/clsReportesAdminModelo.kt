package com.example.educafe.Modelo

class clsReportesAdminModelo {

    suspend fun obtenerVentas(
        periodo: String
    ): RespuestaApi<dataResumenAdmin> {
        return RetrofitClient.apiService.obtenerResumenAdmin(periodo)
    }

    suspend fun obtenerProductos(
        periodo: String
    ): RespuestaApi<dataMasVendidos> {
        return RetrofitClient.apiService.obtenerMasVendidos(periodo)
    }
}