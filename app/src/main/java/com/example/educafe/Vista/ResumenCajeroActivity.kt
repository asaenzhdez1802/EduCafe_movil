package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educafe.R
import com.example.educafe.Modelo.dataPedidos
import com.example.educafe.Presentador.VistaResumenCajero
import com.example.educafe.Presentador.clsResumenCajeroPresentador

class ResumenCajeroActivity :
    AppCompatActivity(), VistaResumenCajero {

    private lateinit var presentador: clsResumenCajeroPresentador
    private lateinit var txtEstadoPedidos: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar el diseño cajero
        setContentView(R.layout.activity_resumen_cajero)

        txtEstadoPedidos = findViewById(R.id.txtEstadoPedidos)

        presentador = clsResumenCajeroPresentador(this, lifecycleScope)
        //consulta los pedidos
        presentador.cargarResumen()

        // Abre  Pedidos del cajero
        findViewById<Button>(R.id.btnVerPedidos).setOnClickListener { abrirPedidos() }

        findViewById<TextView>(R.id.navPedidosCajero).setOnClickListener { abrirPedidos() }

        // Funciones compartidas pendientes
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener { mostrarAviso("Cierre de sesión pendiente de integración") }

        findViewById<TextView>(R.id.navMisVentasCajero).setOnClickListener { mostrarAviso("Pantalla Mis ventas pendiente") }
    }

    private fun abrirPedidos() {
        val intent = Intent(this, PedidosCajeroActivity::class.java)
        startActivity(intent)
    }

    override fun mostrarCarga() {
        txtEstadoPedidos.text = "Consultando pedidos..."
    }

    override fun mostrarPedidos(pedidos: List<dataPedidos>) {

        // Contar los pedidos pendientes
        val pendientes = pedidos.count { it.estado.trim().equals("pendiente", ignoreCase = true)
        }
        // Contar los pedidos listos
        val listos = pedidos.count { it.estado.trim().equals("listo", ignoreCase = true)
        }
        // Muestra las cantidades en las tarjetas
        findViewById<TextView>(R.id.txtPorPreparar).text = pendientes.toString()
        findViewById<TextView>(R.id.txtPedidosListos).text = listos.toString()
        // Mostrar la cantidad de pedidos recibidos
        txtEstadoPedidos.text = "Se encontraron ${pedidos.size} pedidos"
    }

    override fun mostrarError(mensaje: String) {
        txtEstadoPedidos.text = mensaje
    }

    override fun solicitarLogin() {
        mostrarAviso("Debes iniciar sesión nuevamente")
    }

    private fun mostrarAviso(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}