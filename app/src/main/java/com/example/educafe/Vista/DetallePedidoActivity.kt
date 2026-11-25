package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.educafe.R
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.dataDetallePedido
import com.example.educafe.Presentador.VistaDetallePedido
import com.example.educafe.Presentador.clsDetallePedidoPresentador
import java.text.NumberFormat
import java.util.Locale

class DetallePedidoActivity :
    AppCompatActivity(), VistaDetallePedido {

    private lateinit var presentador: clsDetallePedidoPresentador

    private lateinit var txtMensajeDetalle: TextView
    private lateinit var contenedorProductos: LinearLayout
    private lateinit var btnMarcarListo: Button

    private var idPedido = -1
    private var estadoActual = ""

    private val formatoDinero = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Comprobar sesión
        if (!SesionUsuario.haySesion() || SesionUsuario.obtenerRol()
            ?.equals("cajero", ignoreCase = true) != true) {
            Toast.makeText(this, "Acceso exclusivo para cajeros", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContentView(R.layout.activity_detalle_pedido)

        // Obtener el pedido seleccionado desde K2
        idPedido = intent.getIntExtra("id_pedido", -1)

        // Conectar elementos XML
        txtMensajeDetalle = findViewById(R.id.txtMensajeDetalle)

        contenedorProductos = findViewById(R.id.contenedorProductos)

        btnMarcarListo = findViewById(R.id.btnMarcarListo)

        // No permitir actualizar antes de consultar
        btnMarcarListo.isEnabled = false

        findViewById<Button>(R.id.btnEntregarPedido).visibility = android.view.View.GONE
        presentador = clsDetallePedidoPresentador(this, lifecycleScope)
        // Botón regresar
        findViewById<Button>(R.id.btnVolverDetalle).setOnClickListener {
            finish()
        }

        // Botón Pedido listo
        btnMarcarListo.setOnClickListener {

            if (estadoActual == "pendiente" && idPedido > 0) {
                presentador.marcarPedidoListo(idPedido)
            }
        }

        // NAVEGACIÓN INFERIOR

        // K1 - Resumen
        findViewById<TextView>(R.id.navDetalleResumen).setOnClickListener {
            abrirPantalla(ResumenCajeroActivity::class.java)
        }

        // K2 - Pedidos
        findViewById<TextView>(R.id.navDetallePedidos).setOnClickListener {
            abrirPantalla(PedidosCajeroActivity::class.java)
        }

        // K4 - Mis ventas
        findViewById<TextView>(R.id.navDetalleMisVentas).setOnClickListener {
            abrirPantalla(MisVentasCajeroActivity::class.java)
        }

        // Consultar datos reales del pedido
        if (idPedido > 0) {
            presentador.cargarDetallePedido(idPedido)
        } else {
            mostrarError("No se recibió un pedido válido")
        }
    }

    // Navegar sin acumular pantallas duplicadas
    private fun abrirPantalla(destino: Class<*>) {
        val intent = Intent(this, destino).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        startActivity(intent)
        finish()
    }

    // Mostrar consulta en curso
    override fun mostrarCarga() {

        txtMensajeDetalle.text = "Consultando el detalle del pedido..."
    }

    // Mostrar información recibida de la API
    override fun mostrarDetallePedido(pedido: dataDetallePedido) {
        estadoActual = pedido.estado.trim().lowercase(Locale.ROOT)

        // Título como en el mockup
        findViewById<TextView>(R.id.txtFolioDetalle).text = "Pedido ${pedido.folio}"
        // Estado principal
        val tituloEstado = when (estadoActual) {
            "pendiente" -> "Pedido pendiente"
            "listo" -> "Pedido listo"
            "entregado" -> "Pedido entregado"
            "cancelado" -> "Pedido cancelado"
            else -> "Pedido ${pedido.estado}"
        }

        findViewById<TextView>(R.id.txtEstadoDetalle).text = tituloEstado

        // Código grande, sin repetir la etiqueta del XML
        findViewById<TextView>(R.id.txtCodigoRecogidaDetalle).text = pedido.codigo_recogida

        // Cliente
        findViewById<TextView>(R.id.txtClienteDetalle).text = "Cliente: ${pedido.cliente}"
        // Fecha
        findViewById<TextView>(R.id.txtFechaDetalle).text = "Fecha: ${pedido.fecha}"
        // Estado del pago
        val estadoPago = when (pedido.estado_pago.trim().lowercase(Locale.ROOT)) {
            "por_cobrar" -> "Por cobrar"
            "pagado" -> "Pagado"
            "sin_cobro" -> "Sin cobro"
            else -> pedido.estado_pago
        }

        findViewById<TextView>(R.id.txtEstadoPagoDetalle).text = "Estado del pago: $estadoPago"
        // Total real
        findViewById<TextView>(R.id.txtTotalDetalle).text = "Total: ${formatoDinero.format(pedido.total)}"

        // Observaciones
        findViewById<TextView>(R.id.txtNotasDetalle).text = pedido.notas
            ?.takeIf { it.isNotBlank() }
            ?: "Sin observaciones"
        // Productos
        mostrarProductos(pedido)
        // Solo habilitar si está pendiente
        btnMarcarListo.isEnabled = estadoActual == "pendiente"

        txtMensajeDetalle.text = "Detalle del pedido cargado correctamente"
    }

    // Mostrar lista de productos
    private fun mostrarProductos(pedido: dataDetallePedido) {
        contenedorProductos.removeAllViews()
        if (pedido.productos.isEmpty()) {

            val mensaje = TextView(this).apply {
                text = "No hay productos registrados"
                textSize = 14f

                setTextColor(ContextCompat.getColor(this@DetallePedidoActivity, R.color.cafe_texto_secundario))
            }

            contenedorProductos.addView(mensaje)
            return
        }

        pedido.productos.forEach { producto ->

            // Nombre y cantidad
            val nombre = TextView(this).apply {

                text = "${producto.cantidad} x ${producto.nombre}"

                textSize = 16f

                setTextColor(ContextCompat.getColor(this@DetallePedidoActivity, R.color.cafe_marron))

                setPadding(0, 12, 0, 4)
            }

            // Precio y subtotal
            val precio = TextView(this).apply {
                text = "Precio: ${formatoDinero.format(producto.precio_unitario)}" +
                            "  |  Subtotal: ${formatoDinero.format(producto.subtotal)}"

                textSize = 13f

                setTextColor(ContextCompat.getColor(this@DetallePedidoActivity, R.color.cafe_texto_secundario))

                setPadding(0, 0, 0, 12)
            }

            contenedorProductos.addView(nombre)
            contenedorProductos.addView(precio)
        }
    }

    // Deshabilitar mientras se actualiza
    override fun mostrarActualizando() {

        btnMarcarListo.isEnabled = false

        txtMensajeDetalle.text = "Marcando pedido como listo..."
    }

    // Respuesta correcta del servidor
    override fun mostrarActualizacionCorrecta(
        mensaje: String
    ) {

        estadoActual = "listo"

        findViewById<TextView>(R.id.txtEstadoDetalle).text = "Pedido listo"
        btnMarcarListo.isEnabled = false
        txtMensajeDetalle.text = mensaje
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    // Habilitar solo pedidos pendientes
    override fun habilitarBotonListo() {

        if (!isFinishing && !isDestroyed) {

            btnMarcarListo.isEnabled = estadoActual == "pendiente"
        }
    }

    // Mostrar errores
    override fun mostrarError(mensaje: String) {

        txtMensajeDetalle.text = mensaje

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    // Regresar al login cuando expire la sesión
    override fun solicitarLogin() {

        Toast.makeText(this, "Tu sesión venció. Inicia sesión nuevamente", Toast.LENGTH_LONG).show()
        val intent = Intent(this, clsLoginVista::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        startActivity(intent)
        finish()
    }
}