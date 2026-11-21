package com.example.educafe.Presentador

import com.example.educafe.Modelo.clsApiError
import com.example.educafe.Modelo.clsMenuModelo
import com.example.educafe.Modelo.dataProductos
import com.example.educafe.Vista.MenuActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class clsMenuPresentador(private val vista: MenuActivity) {

    private val modelo = clsMenuModelo()
    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var tarea: Job? = null
    private var numeroConsulta = 0
    private var productos = emptyList<dataProductos>()
    private var busqueda = ""

    // 1. Recuperar las categorías reales.
    fun cargarCategorias() {
        alcance.launch {
            try {
                val respuesta = modelo.obtenerCategorias()

                if (respuesta.status == "success") {
                    vista.mostrarCategorias(respuesta.data.orEmpty())
                } else {
                    vista.mostrarAviso(respuesta.message)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                vista.mostrarAviso(clsApiError.leer(e).mensaje)
            }
        }
    }

    // 2. Consultar productos y evitar resultados de filtros anteriores.
    fun cargarProductos(idCategoria: Int) {
        numeroConsulta++
        val consultaActual = numeroConsulta

        tarea?.cancel()
        productos = emptyList()

        vista.mostrarCarga(true)
        vista.mostrarProductos(productos)

        tarea = alcance.launch {
            try {
                val respuesta = modelo.obtenerProductos(idCategoria)

                if (consultaActual == numeroConsulta) {
                    if (respuesta.status == "success") {
                        productos = respuesta.data.orEmpty()
                        filtrar()
                    } else {
                        vista.mostrarError(respuesta.message)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (consultaActual == numeroConsulta) {
                    val error = clsApiError.leer(e)
                    vista.mostrarError(error.mensaje)

                    if (error.codigo == 401) {
                        vista.solicitarLogin()
                    }
                }
            } finally {
                if (consultaActual == numeroConsulta) {
                    vista.mostrarCarga(false)
                }
            }
        }
    }

    // 3. Buscar dentro de la categoría cargada.
    fun buscar(texto: String) {
        busqueda = texto.trim()
        filtrar()
    }

    private fun filtrar() {
        val resultado = productos.filter {
            it.nombre.contains(busqueda, ignoreCase = true) ||
                    (it.descripcion?.contains(
                        busqueda,
                        ignoreCase = true
                    ) == true)
        }

        vista.mostrarProductos(resultado)
    }

    // 4. Cancelar las tareas cuando se destruye la pantalla.
    fun destruir() {
        numeroConsulta++
        alcance.cancel()
    }
}