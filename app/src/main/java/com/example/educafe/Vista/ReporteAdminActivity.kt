package com.example.educafe.Vista

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.Adaptador.FilaReporte
import com.example.educafe.Adaptador.ReportesAdminAdaptador
import com.example.educafe.Modelo.*
import com.example.educafe.Presentador.clsReportesAdminPresentador
import com.example.educafe.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

abstract class ReporteAdminActivity : AppCompatActivity() {

    protected abstract val esVentas: Boolean

    private lateinit var presentador: clsReportesAdminPresentador
    private val adaptador = ReportesAdminAdaptador()
    private var periodo = "hoy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reporte_admin)

        // 1. Respetar las barras del teléfono.
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.raizReporteAdmin)
        ) { vista, insets ->
            val barras = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            vista.setPadding(
                barras.left,
                barras.top,
                barras.right,
                barras.bottom
            )
            insets
        }

        // 2. Preparar la pantalla correspondiente.
        presentador = clsReportesAdminPresentador(this)

        periodo = savedInstanceState?.getString("periodo")
            ?: if (esVentas) "hoy" else "semana"

        texto(
            R.id.txtTituloAdmin,
            if (esVentas) "Ventas generales" else "Productos"
        )

        findViewById<View>(R.id.txtSaludoAdmin).visibility =
            if (esVentas) View.VISIBLE else View.GONE

        findViewById<RecyclerView>(R.id.rvReporteAdmin).apply {
            layoutManager = LinearLayoutManager(this@ReporteAdminActivity)
            adapter = adaptador
        }

        // 3. Configurar Hoy, Semana y Mes.
        val filtros =
            findViewById<MaterialButtonToggleGroup>(
                R.id.grupoPeriodoAdmin
            )

        filtros.check(
            when (periodo) {
                "semana" -> R.id.btnSemanaAdmin
                "mes" -> R.id.btnMesAdmin
                else -> R.id.btnHoyAdmin
            }
        )

        filtros.addOnButtonCheckedListener { _, id, marcado ->
            if (marcado) {
                periodo = when (id) {
                    R.id.btnSemanaAdmin -> "semana"
                    R.id.btnMesAdmin -> "mes"
                    else -> "hoy"
                }
                cargar()
            }
        }

        findViewById<View>(R.id.btnReintentarAdmin)
            .setOnClickListener {
                cargar()
            }

        configurarBarra()

        CierreSesionCompartido.conectar(
            this,
            findViewById(R.id.btnCerrarSesionCompartida)
        )
    }

    override fun onResume() {
        super.onResume()

        // El servidor valida los permisos.
        if (!SesionUsuario.haySesion()) {
            solicitarLogin()
        } else if (SesionUsuario.obtenerRol() != "admin") {
            Toast.makeText(
                this,
                "Acceso solo para administradores",
                Toast.LENGTH_LONG
            ).show()

            NavegacionCliente.irAlMenu(this)
            finish()
        } else {
            cargar()
        }
    }

    private fun cargar() {
        presentador.cargar(periodo, esVentas)
    }

    private fun texto(id: Int, valor: String) {
        findViewById<TextView>(id).text = valor
    }

    private fun limpiarContenido() {
        adaptador.actualizar(emptyList())
        findViewById<View>(R.id.tarjetaTotalAdmin).visibility = View.GONE
        texto(R.id.txtRangoAdmin, "")
    }

    fun mostrarCarga() {
        limpiarContenido()

        findViewById<View>(R.id.progresoAdmin).visibility = View.VISIBLE
        findViewById<View>(R.id.txtEstadoAdmin).visibility = View.GONE
        findViewById<View>(R.id.btnReintentarAdmin).visibility = View.GONE
    }

    private fun terminarCarga() {
        findViewById<View>(R.id.progresoAdmin).visibility = View.GONE
        findViewById<View>(R.id.txtEstadoAdmin).visibility = View.GONE
        findViewById<View>(R.id.btnReintentarAdmin).visibility = View.GONE
    }

    fun mostrarError(mensaje: String) {
        limpiarContenido()
        terminarCarga()

        texto(R.id.txtEstadoAdmin, mensaje)

        findViewById<View>(R.id.txtEstadoAdmin).visibility = View.VISIBLE
        findViewById<View>(R.id.btnReintentarAdmin).visibility = View.VISIBLE
    }

    // El límite final de la API es exclusivo.
    private fun rango(desde: String, hasta: String): String {
        return runCatching {
            val entrada =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            val salida =
                DateTimeFormatter.ofPattern(
                    "dd MMM yyyy",
                    Locale("es", "MX")
                )

            val inicio =
                LocalDateTime.parse(desde, entrada).toLocalDate()

            val fin =
                LocalDateTime.parse(hasta, entrada)
                    .toLocalDate()
                    .minusDays(1)

            "${inicio.format(salida)} — ${fin.format(salida)}"
        }.getOrElse {
            "Desde $desde · hasta $hasta (exclusivo)"
        }
    }

    fun mostrarVentas(datos: dataResumenAdmin) {
        terminarCarga()

        texto(
            R.id.txtRangoAdmin,
            rango(datos.desde, datos.hastaExclusivo)
        )

        findViewById<View>(R.id.tarjetaTotalAdmin).visibility =
            View.VISIBLE

        texto(
            R.id.txtTotalAdmin,
            clsFormato.dinero(datos.totalVendido, false)
        )

        texto(
            R.id.txtCantidadAdmin,
            "${datos.numVentas} ventas completadas"
        )

        val variacion = datos.variacion

        texto(
            R.id.txtComparacionAdmin,
            if (variacion == null) {
                "Sin comparación"
            } else {
                String.format(
                    Locale.US,
                    "%+.1f%% frente al periodo anterior",
                    variacion
                )
            }
        )

        val filas = mutableListOf(
            FilaReporte(
                "Ventas realizadas",
                "Ventas de caja y pedidos cobrados"
            )
        )

        for (venta in datos.ventas) {
            val detalle = listOfNotNull(
                venta.codigoRecogida,
                venta.cliente ?: "Público general",
                clsFormato.diaYHora(venta.fecha),
                if (venta.origen == "app") "App" else "Caja"
            ).joinToString(" · ")

            filas.add(
                FilaReporte(
                    venta.folioPedido ?: venta.folio,
                    detalle,
                    clsFormato.dinero(venta.total, false)
                )
            )
        }

        if (datos.ventas.isEmpty()) {
            filas.add(
                FilaReporte("Sin ventas en este periodo")
            )
        }

        filas.add(
            FilaReporte(
                "Total del periodo",
                importe = clsFormato.dinero(
                    datos.totalVendido,
                    false
                )
            )
        )

        adaptador.actualizar(filas)
    }

    fun mostrarProductos(datos: dataMasVendidos) {
        terminarCarga()

        texto(
            R.id.txtRangoAdmin,
            rango(datos.desde, datos.hastaExclusivo)
        )

        val filas = mutableListOf(
            FilaReporte(
                "Más vendidos",
                "Ordenados por unidades vendidas"
            )
        )

        val maximo =
            (datos.productos.maxOfOrNull { it.unidades } ?: 0)
                .coerceAtLeast(1)

        datos.productos.forEachIndexed { indice, producto ->
            filas.add(
                FilaReporte(
                    titulo = "${indice + 1}. ${producto.nombre}",
                    detalle = "${producto.unidades} vendidos",
                    importe = clsFormato.dinero(
                        producto.importe,
                        false
                    ),
                    imagen = producto.imagen,
                    mostrarImagen = true,
                    porcentaje = (
                            producto.unidades.toDouble() * 100 / maximo
                            ).toInt()
                )
            )
        }

        if (datos.productos.isEmpty()) {
            filas.add(
                FilaReporte("Sin productos vendidos en este periodo")
            )
        }

        filas.add(
            FilaReporte(
                "Ventas por categoría",
                "Porcentaje de los ingresos del periodo"
            )
        )

        for (categoria in datos.categorias) {
            filas.add(
                FilaReporte(
                    titulo = categoria.nombre,
                    detalle = String.format(
                        Locale.US,
                        "%.1f%% de los ingresos",
                        categoria.porcentaje
                    ),
                    importe = clsFormato.dinero(
                        categoria.importe,
                        false
                    ),
                    porcentaje = categoria.porcentaje.toInt()
                )
            )
        }

        if (datos.categorias.isEmpty()) {
            filas.add(
                FilaReporte("Sin ingresos por categoría")
            )
        }

        adaptador.actualizar(filas)
    }

    fun solicitarLogin() {
        limpiarContenido()

        Toast.makeText(
            this,
            "Inicia sesión con una cuenta de administrador",
            Toast.LENGTH_LONG
        ).show()

        val banderas =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        val abierto = NavegacionCliente.abrir(
            this,
            NavegacionCliente.PANTALLA_LOGIN,
            banderas
        )

        if (!abierto) {
            NavegacionCliente.abrir(
                this,
                NavegacionCliente.PANTALLA_BIENVENIDA,
                banderas
            )
        }

        finish()
    }

    // Navegación compartida del administrador.
    // Conserva el diseño y los colores originales de Manuel.
    private fun configurarBarra() {

        // A1 - Ventas generales
        val ventas =
            findViewById<MaterialButton>(R.id.navVentasAdmin)

        // A2 - Pedidos del administrador
        val pedidos =
            findViewById<MaterialButton>(R.id.navPedidosAdmin)

        // A3 - Productos más vendidos
        val productos =
            findViewById<MaterialButton>(R.id.navProductosAdmin)

        // A4 - Stock del administrador
        val stock =
            findViewById<MaterialButton>(R.id.navStockAdmin)

        // Conservar los colores del diseño original
        listOf(ventas, productos, pedidos, stock).forEach { boton ->

            val activo = when (boton.id) {

                R.id.navVentasAdmin -> esVentas

                R.id.navProductosAdmin -> !esVentas

                else -> false
            }

            val color = ContextCompat.getColor(
                this,
                if (activo) {
                    R.color.cafe_naranja
                } else {
                    R.color.cafe_marron
                }
            )

            boton.setTextColor(color)
            boton.iconTint = ColorStateList.valueOf(color)
        }

        // A1: Abrir ventas generales
        ventas.setOnClickListener {

            if (!esVentas) {

                val intent = Intent(
                    this,
                    VentasAdminActivity::class.java
                )

                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                )

                startActivity(intent)
                finish()
            }
        }

        // A2: Abrir pedidos del administrador
        pedidos.setOnClickListener {

            val intent = Intent(
                this,
                PedidosAdminActivity::class.java
            )

            startActivity(intent)
        }

        // A3: Abrir productos más vendidos
        productos.setOnClickListener {

            if (esVentas) {

                startActivity(
                    Intent(
                        this,
                        MasVendidosActivity::class.java
                    )
                )
            }
        }

        // A4: Abrir stock del administrador
        stock.setOnClickListener {

            val intent = Intent(
                this,
                StockAdminActivity::class.java
            )

            startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("periodo", periodo)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        presentador.cancelar()
        limpiarContenido()
        super.onPause()
    }

    override fun onDestroy() {
        presentador.destruir()
        super.onDestroy()
    }
}