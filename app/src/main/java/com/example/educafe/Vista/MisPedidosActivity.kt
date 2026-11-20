package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.Adaptador.PedidosAdaptador
import com.example.educafe.Modelo.dataPedidoCliente
import com.example.educafe.Presentador.clsMisPedidosPresentador
import com.example.educafe.R


class MisPedidosActivity : AppCompatActivity() {

    private lateinit var btnAtras: FrameLayout
    private lateinit var txtChipHistorial: TextView
    private lateinit var rvPedidos: RecyclerView
    private lateinit var presentador: clsMisPedidosPresentador
    private lateinit var adaptador: PedidosAdaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mis_pedidos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnAtras = findViewById(R.id.btnAtras)
        txtChipHistorial = findViewById(R.id.txtChipHistorial)
        rvPedidos = findViewById(R.id.rvPedidos)

        presentador = clsMisPedidosPresentador(this)
        rvPedidos.layoutManager = LinearLayoutManager(this)
        adaptador = PedidosAdaptador(emptyList(), this)
        rvPedidos.adapter = adaptador

        btnAtras.setOnClickListener { finish() }
        // Temporal: C6 Historial llega en el commit 5
        txtChipHistorial.setOnClickListener { mostrarMensaje("Historial llega en el commit 5") }

        NavegacionCliente.configurarBarra(this, NavegacionCliente.PEDIDOS)
    }

    // Recargar al volver y consultar periódicamente mientras se ve
    override fun onResume() {
        super.onResume()
        presentador.iniciar()
    }

    override fun onPause() {
        presentador.detener()
        super.onPause()
    }

    override fun onDestroy() {
        presentador.destruir()
        super.onDestroy()
    }

    // Sin pedidos activos, la lista queda vacía
    fun mostrarPedidos(pedidos: List<dataPedidoCliente>) {
        adaptador.actualizarPedidos(pedidos)
    }

    // Tarjeta -> C4 con los datos del pedido tocado
    fun abrirPedido(pedido: dataPedidoCliente) {
        val intent = Intent(this, PedidoEnviadoActivity::class.java)
        intent.putExtra(PedidoEnviadoActivity.FOLIO, pedido.folio)
        intent.putExtra(PedidoEnviadoActivity.CODIGO, pedido.codigoRecogida)
        intent.putExtra(PedidoEnviadoActivity.TOTAL, pedido.total)
        intent.putExtra(PedidoEnviadoActivity.FECHA_VENCE, pedido.fechaVence)
        intent.putExtra(PedidoEnviadoActivity.ESTADO, pedido.estado)
        intent.putExtra(PedidoEnviadoActivity.ESTADO_PAGO, pedido.estadoPago)
        intent.putExtra(PedidoEnviadoActivity.DESDE_MIS_PEDIDOS, true)
        startActivity(intent)
    }

    fun sesionVencida() {
        NavegacionCliente.sesionVencida(this)
    }

    fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
