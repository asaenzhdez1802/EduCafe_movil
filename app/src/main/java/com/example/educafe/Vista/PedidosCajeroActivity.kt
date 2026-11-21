package com.example.educafe.Vista

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.R
import com.example.educafe.Adaptador.clsPedidosAdaptador
import com.example.educafe.Modelo.dataPedidos
import com.example.educafe.Presentador.VistaPedidos
import com.example.educafe.Presentador.clsPedidosPresentador

// Pantalla K2: Pedidos del cajero
class PedidosCajeroActivity : AppCompatActivity(), VistaPedidos {

    private lateinit var adaptador: clsPedidosAdaptador
    private lateinit var presentador: clsPedidosPresentador

    private var todosPedidos: List<dataPedidos> = emptyList()
    private var filtroActual: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar el diseño XML
        setContentView(R.layout.activity_pedidos_cajero)

        // Preparar la lista de pedidos
        val listaPedidos = findViewById<RecyclerView>(R.id.rvPedidos)

        listaPedidos.layoutManager = LinearLayoutManager(this)

        adaptador = clsPedidosAdaptador(emptyList())
        listaPedidos.adapter = adaptador

        // Preparar los botones
        val btnTodos = findViewById<Button>(R.id.btnTodos)
        val btnPendientes = findViewById<Button>(R.id.btnPendientes)
        val btnListos = findViewById<Button>(R.id.btnListos)
        val btnEntregados = findViewById<Button>(R.id.btnEntregados)

        // Filtrar los pedidos
        btnTodos.setOnClickListener {
            filtrarPedidos(null)
        }

        btnPendientes.setOnClickListener {
            filtrarPedidos("pendiente")
        }

        btnListos.setOnClickListener {
            filtrarPedidos("listo")
        }

        btnEntregados.setOnClickListener {
            filtrarPedidos("entregado")
        }

        // Crear el Presentador
        presentador = clsPedidosPresentador(this)

        // Consultar los pedidos del servidor
        presentador.cargarPedidos()
    }

    // Filtrar los pedidos recibidos
    private fun filtrarPedidos(estado: String?) {

        filtroActual = estado

        if (estado == null) {
            adaptador.actualizarPedidos(todosPedidos)
        } else {
            val pedidosFiltrados = todosPedidos.filter {
                it.estado == estado
            }

            adaptador.actualizarPedidos(pedidosFiltrados)
        }
    }

    // Mostrar que comienza la consulta
    override fun mostrarCarga() {
        Toast.makeText(
            this,
            "Cargando pedidos...",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Recibir los pedidos del Presentador
    override fun mostrarPedidos(pedidos: List<dataPedidos>) {

        todosPedidos = pedidos

        // Contar pedidos pendientes
        val pendientes = pedidos.count {
            it.estado == "pendiente"
        }

        // Contar pedidos listos
        val listos = pedidos.count {
            it.estado == "listo"
        }

        // Contar pedidos entregados
        val entregados = pedidos.count {
            it.estado == "entregado"
        }

        // Actualizar los contadores
        findViewById<TextView>(R.id.txtPendientes).text =
            pendientes.toString()

        findViewById<TextView>(R.id.txtListos).text =
            listos.toString()

        findViewById<TextView>(R.id.txtEntregados).text =
            entregados.toString()

        // Mostrar los pedidos
        filtrarPedidos(filtroActual)
    }

    // Mostrar un error
    override fun mostrarError(mensaje: String) {
        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    // Avisar cuando se necesita iniciar sesión
    override fun solicitarLogin() {
        Toast.makeText(
            this,
            "Debes iniciar sesión nuevamente",
            Toast.LENGTH_LONG
        ).show()
    }
}