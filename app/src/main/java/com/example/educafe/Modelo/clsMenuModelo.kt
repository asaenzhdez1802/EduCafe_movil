package com.example.educafe.Modelo

class clsMenuModelo {

    // 1. Consultar las categorías.
    suspend fun obtenerCategorias(): RespuestaApi<List<dataCategorias>> {
        return RetrofitClient.apiService.obtenerCategorias()
    }

    // 2. Consultar los productos de la categoría elegida.
    suspend fun obtenerProductos(
        idCategoria: Int
    ): RespuestaApi<List<dataProductos>> {
        return RetrofitClient.apiService.obtenerProductos(idCategoria)
    }
}