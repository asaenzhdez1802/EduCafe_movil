package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.Adaptador.CarritoAdaptador
import com.example.educafe.Modelo.clsFormato
import com.example.educafe.Modelo.dataArticuloCarrito
import com.example.educafe.Presentador.clsTuPedidoPresentador
import com.example.educafe.R

class TuPedidoActivity : AppCompatActivity() {

    companion object {
        // P2 lo envía en true al tocar + para mostrar la etiqueta "Agregado"
        const val PRODUCTO_AGREGADO = "PRODUCTO_AGREGADO"
    }

    private lateinit var txtNumProductos: TextView
    private lateinit var lblAgregado: LinearLayout
    private lateinit var rvArticulos: RecyclerView
    private lateinit var txtSubtotal: TextView
    private lateinit var txtServicio: TextView
    private lateinit var txtTotal: TextView
    private lateinit var btnContinuar: Button
    private lateinit var btnSeguirComprando: Button
    private lateinit var btnCancelarPedido: Button
    private lateinit var presentador: clsTuPedidoPresentador
    private lateinit var adaptador: CarritoAdaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tu_pedido)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtNumProductos = findViewById(R.id.txtNumProductos)
        lblAgregado = findViewById(R.id.lblAgregado)
        rvArticulos = findViewById(R.id.rvArticulos)
        txtSubtotal = findViewById(R.id.txtSubtotal)
        txtServicio = findViewById(R.id.txtServicio)
        txtTotal = findViewById(R.id.txtTotal)
        btnContinuar = findViewById(R.id.btnContinuar)
        btnSeguirComprando = findViewById(R.id.btnSeguirComprando)
        btnCancelarPedido = findViewById(R.id.btnCancelarPedido)

        presentador = clsTuPedidoPresentador(this)
        rvArticulos.layoutManager = LinearLayoutManager(this)
        adaptador = CarritoAdaptador(emptyList(), this)
        rvArticulos.adapter = adaptador

        mostrarEtiquetaAgregado(intent)

        btnContinuar.setOnClickListener { presentador.continuar() }
        btnSeguirComprando.setOnClickListener { presentador.seguirComprando() }
        btnCancelarPedido.setOnClickListener { presentador.cancelarCarrito() }

        NavegacionCliente.configurarBarra(this, NavegacionCliente.MENU)
    }

    override fun onResume() {
        super.onResume()
        presentador.cargar()
    }

    private fun mostrarEtiquetaAgregado(intent: Intent) {
        val agregado = intent.getBooleanExtra(PRODUCTO_AGREGADO, false)
        lblAgregado.visibility = if (agregado) View.VISIBLE else View.GONE
    }

    fun mostrarArticulos(articulos: List<dataArticuloCarrito>, piezas: Int, subtotal: Double) {
        adaptador.actualizarArticulos(articulos)
        txtNumProductos.text = getString(R.string.productos_en_pedido, piezas)
        mostrarTotales(subtotal)
    }


    fun mostrarVacio() {
        adaptador.actualizarArticulos(emptyList())
        lblAgregado.visibility = View.GONE
        txtNumProductos.text = getString(R.string.productos_en_pedido, 0)
        mostrarTotales(0.0)
    }

    private fun mostrarTotales(subtotal: Double) {
        txtSubtotal.text = clsFormato.dinero(subtotal)
        txtServicio.text = clsFormato.dinero(0.0)
        txtTotal.text = clsFormato.dinero(subtotal)
    }

    // Llamados por el adaptador
    fun aumentar(articulo: dataArticuloCarrito) {
        presentador.aumentar(articulo)
    }

    fun disminuir(articulo: dataArticuloCarrito) {
        presentador.disminuir(articulo)
    }


    fun irAConfirmar() {
        startActivity(Intent(this, ConfirmarPedidoActivity::class.java))
    }


    fun irALoginParaPagar() {
        NavegacionCliente.irALoginParaPagar(this)
    }

    fun cerrar() {
        finish()
    }

    fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
