package com.example.educafe.Vista

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.educafe.Modelo.clsFormato
import com.example.educafe.R


class PedidoEnviadoActivity : AppCompatActivity() {

    companion object {
        const val FOLIO = "FOLIO"
        const val CODIGO = "CODIGO"
        const val TOTAL = "TOTAL"
        const val FECHA_VENCE = "FECHA_VENCE"
        const val ESTADO = "ESTADO"
        const val ESTADO_PAGO = "ESTADO_PAGO"
        // true cuando se abrió desde una tarjeta de C5
        const val DESDE_MIS_PEDIDOS = "DESDE_MIS_PEDIDOS"
    }

    private lateinit var txtAvisoPago: TextView
    private lateinit var txtCodigo: TextView
    private lateinit var txtFolio: TextView
    private lateinit var txtEstadoPedido: TextView
    private lateinit var txtTotalPagar: TextView
    private lateinit var btnListo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedido_enviado)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtAvisoPago = findViewById(R.id.txtAvisoPago)
        txtCodigo = findViewById(R.id.txtCodigo)
        txtFolio = findViewById(R.id.txtFolio)
        txtEstadoPedido = findViewById(R.id.txtEstadoPedido)
        txtTotalPagar = findViewById(R.id.txtTotalPagar)
        btnListo = findViewById(R.id.btnListo)

        val folio = intent.getStringExtra(FOLIO) ?: ""
        val codigo = intent.getStringExtra(CODIGO) ?: ""
        val total = intent.getDoubleExtra(TOTAL, 0.0)
        val fechaVence = intent.getStringExtra(FECHA_VENCE)
        val estado = intent.getStringExtra(ESTADO) ?: "pendiente"
        val estadoPago = intent.getStringExtra(ESTADO_PAGO) ?: "por_cobrar"

        txtCodigo.text = codigo
        txtFolio.text = "Pedido: $folio"
        txtEstadoPedido.text = "Estado: ${clsFormato.estado(estado)} · ${clsFormato.estadoPago(estadoPago)}"
        txtTotalPagar.text = "Total a pagar en cafetería: ${clsFormato.dinero(total)}"
        txtAvisoPago.text = "Apartamos tus productos por 60 minutos. Págalo en efectivo en caja antes de las " +
            "${clsFormato.hora(fechaVence)} o se cancelará."

        btnListo.setOnClickListener { terminar() }

        // Atrás hace lo mismo que Listo: nunca regresa a C3 (el pedido ya se envió)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                terminar()
            }
        })

        NavegacionCliente.configurarBarra(this, NavegacionCliente.PEDIDOS)
    }

    private fun terminar() {
        if (!intent.getBooleanExtra(DESDE_MIS_PEDIDOS, false)) {
            NavegacionCliente.irAMisPedidosTrasPedido(this)
        }
        finish()
    }
}
