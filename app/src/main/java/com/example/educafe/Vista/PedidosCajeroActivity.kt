package com.example.educafe.Vista

import android.content.Intent
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

class PedidosCajeroActivity :
    AppCompatActivity(), VistaPedidos {

    private lateinit var adaptador: clsPedidosAdaptador
    private lateinit var presentador: clsPedidosPresentador

    private var todosPedidos: List<dataPedidos> = emptyList()
    private var filtroActual: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar el diseño XML de K2
        setContentView(R.layout.activity_pedidos_cajero)
        val listaPedidos = findViewById<RecyclerView>(R.id.rvPedidos)

        listaPedidos.layoutManager = LinearLayoutManager(this)

        // Adaptador que detecta el pedido seleccionado
        adaptador = clsPedidosAdaptador(emptyList()) { idPedido ->
            abrirDetallePedido(idPedido)
        }

        listaPedidos.adapter = adaptador

        // Preparar los botones de filtros
        val btnTodos = findViewById<Button>(R.id.btnTodos)
        val btnPendientes = findViewById<Button>(R.id.btnPendientes)
        val btnListos = findViewById<Button>(R.id.btnListos)
        val btnEntregados = findViewById<Button>(R.id.btnEntregados)

        // Filtrar todos los pedidos
        btnTodos.setOnClickListener {
            filtrarPedidos(null)
        }

        // Filtrar pedidos pendientes
        btnPendientes.setOnClickListener {
            filtrarPedidos("pendiente")
        }

        // Filtrar pedidos listos
        btnListos.setOnClickListener {
            filtrarPedidos("listo")
        }

        // Filtrar pedidos entregados
        btnEntregados.setOnClickListener {
            filtrarPedidos("entregado")
        }

        // Crear el Presentador MVP
        presentador = clsPedidosPresentador(this)

        // Consultar los pedidos mediante la API real
        presentador.cargarPedidos()
    }

    // Abrir K3 con el identificador del pedido
    private fun abrirDetallePedido(idPedido: Int) {

        if (idPedido <= 0) {
            mostrarError("El pedido seleccionado no es válido")
            return
        }

        val intent = Intent(this, DetallePedidoActivity::class.java)
        // Enviar el ID a la pantalla K3
        intent.putExtra("id_pedido", idPedido)

        startActivity(intent)
    }

    // Filtrar los pedidos recibidos de la API
    private fun filtrarPedidos(estado: String?) {

        filtroActual = estado

        if (estado == null) {

            adaptador.actualizarPedidos(todosPedidos)

        } else {

            val pedidosFiltrados = todosPedidos.filter {
                it.estado.trim().equals(estado, ignoreCase = true)
            }

            adaptador.actualizarPedidos(pedidosFiltrados)
        }
    }

    // Mostrar que comienza la consulta
    override fun mostrarCarga() {
        Toast.makeText(this, "Cargando pedidos...", Toast.LENGTH_SHORT).show()
    }

    // Recibir pedidos del Presentador
    override fun mostrarPedidos(pedidos: List<dataPedidos>) {

        todosPedidos = pedidos

        // Contar pedidos pendientes
        val pendientes = pedidos.count {
            it.estado.trim().equals("pendiente", ignoreCase = true)
        }

        // Contar pedidos listos
        val listos = pedidos.count {
            it.estado.trim().equals("listo", ignoreCase = true)
        }

        val entregados = pedidos.count {
            it.estado.trim().equals("entregado", ignoreCase = true)
        }

        findViewById<TextView>(R.id.txtPendientes).text = pendientes.toString()
        findViewById<TextView>(R.id.txtListos).text = listos.toString()
        findViewById<TextView>(R.id.txtEntregados).text = entregados.toString()

        filtrarPedidos(filtroActual)
    }

    // Mostrar errores de la API
    override fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    // Avisar cuando la sesión haya vencido
    override fun solicitarLogin() {
        Toast.makeText(this, "Debes iniciar sesión nuevamente", Toast.LENGTH_LONG).show()
    }
}