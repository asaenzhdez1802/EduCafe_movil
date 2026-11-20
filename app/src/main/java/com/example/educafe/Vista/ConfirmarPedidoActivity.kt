package com.example.educafe.Vista

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.educafe.Modelo.clsFormato
import com.example.educafe.Modelo.dataPedidoRegistrado
import com.example.educafe.Presentador.clsConfirmarPedidoPresentador
import com.example.educafe.R

class ConfirmarPedidoActivity : AppCompatActivity() {

    private lateinit var btnAtras: FrameLayout
    private lateinit var txtTotalConfirmar: TextView
    private lateinit var btnConfirmar: Button
    private lateinit var btnVolver: Button
    private lateinit var presentador: clsConfirmarPedidoPresentador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmar_pedido)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnAtras = findViewById(R.id.btnAtras)
        txtTotalConfirmar = findViewById(R.id.txtTotalConfirmar)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        btnVolver = findViewById(R.id.btnVolver)

        presentador = clsConfirmarPedidoPresentador(this)

        btnConfirmar.setOnClickListener { presentador.confirmar() }
        btnVolver.setOnClickListener { volver() }
        btnAtras.setOnClickListener { volver() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                volver()
            }
        })

        presentador.mostrarResumen()
    }

    override fun onDestroy() {
        presentador.destruir()
        super.onDestroy()
    }
    private fun volver() {
        if (presentador.estaEnviando()) return
        finish()
    }

    fun mostrarTotal(subtotal: Double) {
        txtTotalConfirmar.text = clsFormato.dinero(subtotal)
    }


    fun mostrarEnviando(enviando: Boolean) {
        btnConfirmar.isEnabled = !enviando
        btnVolver.isEnabled = !enviando
        btnAtras.isEnabled = !enviando
    }

    fun pedidoRegistrado(pedido: dataPedidoRegistrado) {
        mostrarMensaje("Pedido ${pedido.folio} · código ${pedido.codigoRecogida}")
        NavegacionCliente.irAlMenu(this)
        finish()
    }

    fun volverAlPedido() {
        finish()
    }

    fun sesionVencida() {
        NavegacionCliente.sesionVencida(this)
    }

    fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
