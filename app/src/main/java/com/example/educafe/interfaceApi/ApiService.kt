package com.example.educafe.interfaceApi

import com.example.educafe.Modelo.RespuestaApi
import com.example.educafe.Modelo.dataCategorias
import retrofit2.http.GET

interface ApiService {

    // 1. Consultar las categorías del menú
    @GET("obtener_categorias.php")
    suspend fun obtenerCategorias(): RespuestaApi<List<dataCategorias>>

}