package com.example.educafe.Vista

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.R
import com.example.educafe.Adaptador.clsMisVentasCajeroAdaptador
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.dataMisVentasCajero
import com.example.educafe.Modelo.dataVentaCajero
import com.example.educafe.Presentador.VistaMisVentasCajero
import com.example.educafe.Presentador.clsMisVentasCajeroPresentador
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

// K4 - Mis ventas del cajero
class MisVentasCajeroActivity :
    AppCompatActivity(), VistaMisVentasCajero {

    private lateinit var adaptador: clsMisVentasCajeroAdaptador
    private lateinit var presentador: clsMisVentasCajeroPresentador

    private lateinit var txtEstado: TextView
    private lateinit var txtCantidad: TextView
    private lateinit var txtTotal: TextView
    private lateinit var txtFecha: TextView

    private var fechaSeleccionada: String? = null

    private val formatoDinero = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Validar sesión de cajero
        if (!SesionUsuario.haySesion() || SesionUsuario.obtenerRol()?.equals("cajero", ignoreCase = true) != true) {
            Toast.makeText(this, "Acceso exclusivo para cajeros", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContentView(R.layout.activity_mis_ventas_cajero)

        fechaSeleccionada = savedInstanceState?.getString("fecha_seleccionada")

        // Elementos del XML
        txtEstado = findViewById(R.id.txtEstadoMisVentas)
        txtCantidad = findViewById(R.id.txtCantidadMisVentas)
        txtTotal = findViewById(R.id.txtTotalMisVentas)
        txtFecha = findViewById(R.id.txtFechaMisVentas)

        actualizarTituloFecha()
        // Configurar historial
        val recycler = findViewById<RecyclerView>(R.id.rvMisVentas)
        recycler.layoutManager = LinearLayoutManager(this)

        adaptador = clsMisVentasCajeroAdaptador { venta ->
            mostrarResumenVenta(venta)
        }

        recycler.adapter = adaptador

        // Presentador MVP
        presentador = clsMisVentasCajeroPresentador(this, lifecycleScope)
        // Consultar ventas de hoy
        findViewById<Button>(R.id.btnHoyVentas).setOnClickListener {
                fechaSeleccionada = null
                actualizarTituloFecha()
                presentador.cargarVentas()
            }

        // Filtrar por fecha
        findViewById<Button>(R.id.btnElegirFechaVentas).setOnClickListener {
            elegirFecha()
        }

        // Botón de regresar conservado
        findViewById<Button>(R.id.btnRegresarMisVentas).setOnClickListener {
                finish()
        }

        configurarNavegacion()
    }

    // Consultar API al entrar o regresar a K4
    override fun onResume() {
        super.onResume()

        if (::presentador.isInitialized) {
            presentador.cargarVentas(fechaSeleccionada)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fecha_seleccionada", fechaSeleccionada)
    }

    // Barra inferior con íconos originales
    private fun configurarNavegacion() {

        // K1 - Resumen
        findViewById<TextView>(R.id.navMisVentasResumen).setOnClickListener {
            abrirPantalla(ResumenCajeroActivity::class.java)
        }

        // K2 - Pedidos
        findViewById<TextView>(R.id.navMisVentasPedidos).setOnClickListener {
            abrirPantalla(PedidosCajeroActivity::class.java)
        }

        // K4 - Actualizar Mis ventas
        findViewById<TextView>(R.id.navMisVentasActual).setOnClickListener {
            presentador.cargarVentas(fechaSeleccionada)
        }
    }

    private fun abrirPantalla(destino: Class<*>) {

        val intent = Intent(this, destino).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        startActivity(intent)
        finish()
    }

    // Título de la tarjeta según fecha
    private fun actualizarTituloFecha() {

        txtFecha.text =
            if (fechaSeleccionada == null) {
                "Total cobrado hoy"
            } else {
                "Total cobrado del $fechaSeleccionada"
            }
    }

    // Seleccionar fecha para la consulta
    private fun elegirFecha() {

        val calendario = Calendar.getInstance()

        fechaSeleccionada?.let { fecha ->
            try {

                val partes = fecha.split("-")

                calendario.set(partes[0].toInt(), partes[1].toInt() - 1, partes[2].toInt())

            } catch (_: Exception) {
                calendario.timeInMillis = System.currentTimeMillis()
            }
        }

        DatePickerDialog(this, { _, anio, mes, dia ->
                fechaSeleccionada = String.format(Locale.US, "%04d-%02d-%02d", anio, mes + 1, dia)

                actualizarTituloFecha()

                presentador.cargarVentas(fechaSeleccionada)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Consultando el servidor
    override fun mostrarCarga() {

        txtEstado.text = "Consultando ventas..."
    }

    // Mostrar datos de obtener_mis_ventas.php
    override fun mostrarVentas(datos: dataMisVentasCajero) {

        adaptador.actualizarVentas(datos.ventas)

        // Cantidad real de ventas
        txtCantidad.text =
            "${datos.ventas.size} ventas registradas"

        // Total recibido de la API
        txtTotal.text = "${formatoDinero.format(datos.total_cobrado)} MXN"

        txtEstado.text =
            if (datos.ventas.isEmpty()) {
                "No se encontraron ventas para esta fecha"
            } else {
                ""
            }
    }

    // Error del servidor o de conexión
    override fun mostrarError(mensaje: String) {

        adaptador.actualizarVentas(emptyList())

        txtCantidad.text = "0 ventas registradas"

        txtTotal.text = "${formatoDinero.format(0.0)} MXN"

        txtEstado.text = mensaje

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    // Sesión vencida
    override fun solicitarLogin() {

        Toast.makeText(this, "Tu sesión venció. Inicia sesión nuevamente", Toast.LENGTH_LONG).show()

        val intent = Intent(this, clsLoginVista::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        startActivity(intent)
        finish()
    }

    // Mostrar detalles de la venta seleccionada
    private fun mostrarResumenVenta(
        venta: dataVentaCajero
    ) {

        val cliente = venta.cliente?.takeIf { it.isNotBlank() } ?: "Sin cuenta"

        val folioPedido = venta.folio_pedido?.takeIf { it.isNotBlank() } ?: "Sin pedido asociado"

        val mensaje = buildString {
            appendLine("Folio: ${venta.folio}")
            appendLine("Fecha: ${venta.fecha}")
            appendLine("Cliente: $cliente")
            appendLine("Origen: ${venta.origen}")
            appendLine("Pedido: $folioPedido")
            appendLine("Estado: ${venta.estado}")
            appendLine()
            appendLine("Total: ${formatoDinero.format(venta.total)}")
            appendLine("Puntos ganados: ${venta.puntos_ganados}")
        }

        AlertDialog.Builder(this)
            .setTitle("Detalle de venta")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar", null)
            .show()
    }
}