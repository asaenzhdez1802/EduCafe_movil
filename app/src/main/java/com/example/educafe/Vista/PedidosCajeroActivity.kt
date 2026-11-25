package com.example.educafe.Vista

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.Adaptador.clsPedidosAdaptador
import com.example.educafe.Modelo.dataPedidos
import com.example.educafe.Presentador.VistaPedidos
import com.example.educafe.Presentador.clsPedidosPresentador
import com.example.educafe.R
import com.google.android.material.button.MaterialButton

class PedidosCajeroActivity : AppCompatActivity(), VistaPedidos {

    private lateinit var adaptador: clsPedidosAdaptador
    private lateinit var presentador: clsPedidosPresentador
    private lateinit var filtros: Map<MaterialButton, String?>

    private var todosPedidos: List<dataPedidos> = emptyList()
    private var filtroActual: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos_cajero)

        val listaPedidos = findViewById<RecyclerView>(R.id.rvPedidos)
        listaPedidos.layoutManager = LinearLayoutManager(this)

        adaptador = clsPedidosAdaptador(emptyList()) { idPedido ->
            abrirDetallePedido(idPedido)
        }
        listaPedidos.adapter = adaptador

        // El filtro Todos usa null para mostrar la lista completa.
        filtros = mapOf(
            findViewById<MaterialButton>(R.id.btnTodos) to null,
            findViewById<MaterialButton>(R.id.btnPendientes) to "pendiente",
            findViewById<MaterialButton>(R.id.btnListos) to "listo",
            findViewById<MaterialButton>(R.id.btnEntregados) to "entregado"
        )

        filtros.forEach { (boton, estado) ->
            boton.setOnClickListener { filtrarPedidos(estado) }
        }

        filtrarPedidos(savedInstanceState?.getString("filtro_actual"))

        // Barra inferior de la pantalla Pedidos.
        findViewById<TextView>(R.id.navResumenCajero).setOnClickListener {
            abrirSeccion(ResumenCajeroActivity::class.java)
        }

        findViewById<TextView>(R.id.navMisVentasCajero).setOnClickListener {
            abrirSeccion(MisVentasCajeroActivity::class.java)
        }

        presentador = clsPedidosPresentador(this)
        presentador.cargarPedidos()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("filtro_actual", filtroActual)
    }

    private fun abrirSeccion(destino: Class<*>) {
        val intent = Intent(this, destino).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }
        startActivity(intent)
        finish()
    }

    private fun abrirDetallePedido(idPedido: Int) {
        if (idPedido <= 0) {
            mostrarError("El pedido seleccionado no es válido")
            return
        }

        val intent = Intent(this, DetallePedidoActivity::class.java)
        intent.putExtra("id_pedido", idPedido)
        startActivity(intent)
    }

    private fun filtrarPedidos(estado: String?) {
        filtroActual = estado

        // Solo el filtro seleccionado tiene fondo naranja y texto blanco.
        filtros.forEach { (boton, filtro) ->
            val activo = filtro == estado
            val fondo = if (activo) R.color.cafe_naranja else R.color.cafe_blanco
            val texto = if (activo) R.color.cafe_blanco else R.color.cafe_marron

            boton.isSelected = activo
            boton.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, fondo)
            )
            boton.setTextColor(ContextCompat.getColor(this, texto))
        }

        val pedidosVisibles = if (estado == null) {
            todosPedidos
        } else {
            todosPedidos.filter {
                it.estado.trim().equals(estado, ignoreCase = true)
            }
        }

        adaptador.actualizarPedidos(pedidosVisibles)
    }

    override fun mostrarCarga() {
        Toast.makeText(this, "Cargando pedidos...", Toast.LENGTH_SHORT).show()
    }

    override fun mostrarPedidos(pedidos: List<dataPedidos>) {
        todosPedidos = pedidos

        val pendientes = pedidos.count {
            it.estado.trim().equals("pendiente", ignoreCase = true)
        }
        val listos = pedidos.count {
            it.estado.trim().equals("listo", ignoreCase = true)
        }
        val entregados = pedidos.count {
            it.estado.trim().equals("entregado", ignoreCase = true)
        }

        findViewById<TextView>(R.id.txtPendientes).text = pendientes.toString()
        findViewById<TextView>(R.id.txtListos).text = listos.toString()
        findViewById<TextView>(R.id.txtEntregados).text = entregados.toString()

        // Mantener el filtro seleccionado cuando llegan los datos de la API.
        filtrarPedidos(filtroActual)
    }

    override fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    override fun solicitarLogin() {
        Toast.makeText(
            this,
            "Debes iniciar sesión nuevamente",
            Toast.LENGTH_LONG
        ).show()
    }
}