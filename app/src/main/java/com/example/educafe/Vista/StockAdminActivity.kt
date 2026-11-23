package com.example.educafe.Vista

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.educafe.R
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.dataStockAdmin
import com.example.educafe.Presentador.VistaStockAdmin
import com.example.educafe.Presentador.clsStockAdminPresentador
import com.google.android.material.button.MaterialButton

// A4 - Stock del administrador
class StockAdminActivity :
    AppCompatActivity(), VistaStockAdmin {

    private lateinit var presentador: clsStockAdminPresentador

    private lateinit var txtEstado: TextView
    private lateinit var txtTotalAlertas: TextView
    private lateinit var contenedor: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Validar que exista una sesión de administrador
        if (!SesionUsuario.haySesion() ||
            SesionUsuario.obtenerRol()
                ?.equals("admin", ignoreCase = true) != true
        ) {
            Toast.makeText(
                this,
                "Acceso exclusivo para administradores",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        // Cargar el diseño original de A4
        setContentView(R.layout.activity_stock_admin)

        // Conectar los elementos existentes del XML
        txtEstado = findViewById(R.id.txtEstadoStock)

        txtTotalAlertas =
            findViewById(R.id.txtTotalAlertasStock)

        contenedor =
            findViewById(R.id.contenedorProductosStock)

        // Crear presentador siguiendo MVP
        presentador = clsStockAdminPresentador(
            this,
            lifecycleScope
        )

        // Botón para actualizar inventario
        findViewById<MaterialButton>(
            R.id.btnActualizarStock
        ).setOnClickListener {

            presentador.cargarStock()
        }

        // Regresar a la pantalla anterior
        findViewById<MaterialButton>(
            R.id.btnRegresarStock
        ).setOnClickListener {

            finish()
        }

        // Mantener barra inferior e íconos originales
        configurarBarra()
    }

    // Consultar al entrar y al regresar a A4
    override fun onResume() {
        super.onResume()

        if (::presentador.isInitialized) {
            presentador.cargarStock()
        }
    }

    // Barra inferior del administrador
    private fun configurarBarra() {

        val ventas =
            findViewById<MaterialButton>(
                R.id.navStockVentas
            )

        val pedidos =
            findViewById<MaterialButton>(
                R.id.navStockPedidos
            )

        val productos =
            findViewById<MaterialButton>(
                R.id.navStockProductos
            )

        val stock =
            findViewById<MaterialButton>(
                R.id.navStockActual
            )

        // Conservar colores e íconos existentes
        listOf(
            ventas,
            pedidos,
            productos,
            stock
        ).forEach { boton ->

            val color = ContextCompat.getColor(
                this,
                if (boton.id == R.id.navStockActual) {
                    R.color.cafe_naranja
                } else {
                    R.color.cafe_marron
                }
            )

            boton.setTextColor(color)

            boton.iconTint =
                ColorStateList.valueOf(color)
        }

        // A1 - Ventas
        ventas.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    VentasAdminActivity::class.java
                )
            )

            finish()
        }

        // A2 - Pedidos
        pedidos.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PedidosAdminActivity::class.java
                )
            )

            finish()
        }

        // A3 - Productos más vendidos
        productos.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MasVendidosActivity::class.java
                )
            )

            finish()
        }

        // A4 - Actualizar la pantalla actual
        stock.setOnClickListener {
            presentador.cargarStock()
        }
    }

    // Mostrar carga
    override fun mostrarCarga() {

        txtEstado.text =
            "Consultando inventario..."

        txtTotalAlertas.text = "—"
    }

    // Recibir datos reales de obtener_stock.php
    override fun mostrarStock(
        productos: List<dataStockAdmin>
    ) {

        // Limpiar registros anteriores
        contenedor.removeAllViews()

        // Número de productos en alerta
        txtTotalAlertas.text =
            productos.size.toString()

        // Sin alertas
        if (productos.isEmpty()) {

            txtEstado.text =
                "No se encontraron productos con stock bajo o agotado"

            return
        }

        // Mostrar cantidad encontrada
        txtEstado.text =
            "Se encontraron ${productos.size} productos en alerta"

        // Crear tarjetas con datos de la API
        productos.forEach { producto ->

            agregarTarjetaProducto(producto)
        }
    }

    // Crear tarjeta de un producto
    private fun agregarTarjetaProducto(
        producto: dataStockAdmin
    ) {

        val tarjeta = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            setPadding(
                dp(16),
                dp(14),
                dp(16),
                dp(14)
            )

            // Drawable ya existente en Edu Café
            setBackgroundResource(
                R.drawable.bg_tarjeta
            )

            layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(12)
                }
        }

        // Nombre real del producto
        val nombre = crearTexto(
            producto.nombre,
            18f,
            true,
            R.color.cafe_marron
        )

        // Código del producto
        val codigo = crearTexto(
            "Código: ${producto.codigo}",
            14f
        )

        // Existencias registradas
        val existencias = crearTexto(
            "Existencias: ${producto.stock}",
            14f
        )

        // Unidades reservadas
        val reservado = crearTexto(
            "Reservadas: ${producto.stock_reservado}",
            14f
        )

        // Cantidad realmente disponible
        val disponible = crearTexto(
            "Disponibles: ${producto.disponibles}",
            16f,
            true,
            R.color.cafe_marron
        )

        // Límite mínimo configurado
        val minimo = crearTexto(
            "Stock mínimo: ${producto.stock_minimo}",
            14f
        )

        // Identificar productos agotados
        val agotado =
            producto.disponibles <= 0

        val estado = crearTexto(
            if (agotado) {
                "AGOTADO"
            } else {
                "Estado: ${producto.estado_stock}"
            },
            15f,
            true,
            R.color.cafe_naranja
        )

        // Agregar información a la tarjeta
        tarjeta.addView(nombre)
        tarjeta.addView(codigo)
        tarjeta.addView(existencias)
        tarjeta.addView(reservado)
        tarjeta.addView(disponible)
        tarjeta.addView(minimo)
        tarjeta.addView(estado)

        // Mostrar tarjeta en pantalla
        contenedor.addView(tarjeta)
    }

    // Crear textos utilizando colors.xml
    private fun crearTexto(
        contenido: String,
        tamano: Float,
        negritas: Boolean = false,
        @ColorRes color: Int =
            R.color.cafe_texto_secundario
    ): TextView {

        return TextView(this).apply {

            text = contenido
            textSize = tamano

            setTextColor(
                ContextCompat.getColor(
                    this@StockAdminActivity,
                    color
                )
            )

            if (negritas) {
                setTypeface(
                    null,
                    Typeface.BOLD
                )
            }

            setPadding(
                0,
                dp(3),
                0,
                dp(3)
            )
        }
    }

    // Convertir dp a píxeles
    private fun dp(valor: Int): Int {

        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }

    // Mostrar errores de la API
    override fun mostrarError(mensaje: String) {

        txtEstado.text = mensaje
        txtTotalAlertas.text = "—"

        contenedor.removeAllViews()

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    // Manejar sesión vencida
    override fun solicitarLogin() {

        Toast.makeText(
            this,
            "Tu sesión venció. Inicia sesión nuevamente",
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(
            this,
            clsLoginVista::class.java
        ).apply {

            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        }

        startActivity(intent)
        finish()
    }
}