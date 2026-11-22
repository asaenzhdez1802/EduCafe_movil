package com.example.educafe.Vista

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educafe.R
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.dataDetallePedido
import com.example.educafe.Presentador.VistaDetallePedido
import com.example.educafe.Presentador.clsDetallePedidoPresentador
import java.util.Locale

// K3 - Detalle del pedido del cajero
class DetallePedidoActivity :
    AppCompatActivity(), VistaDetallePedido {

    private lateinit var presentador: clsDetallePedidoPresentador
    private lateinit var txtMensajeDetalle: TextView
    private lateinit var contenedorProductos: LinearLayout

    private var idPedido: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar el diseño XML
        setContentView(R.layout.activity_detalle_pedido)

        // Validar sesión de cajero
        if (!SesionUsuario.haySesion() || SesionUsuario.obtenerRol()?.equals("cajero", ignoreCase = true) != true) {
            Toast.makeText(this, "Acceso exclusivo para cajeros", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Obtener el ID enviado desde K2
        idPedido = intent.getIntExtra("id_pedido", -1)
        // Preparar los componentes
        txtMensajeDetalle = findViewById(R.id.txtMensajeDetalle)
        contenedorProductos = findViewById(R.id.contenedorProductos)
        // Botón para regresar a K2
        findViewById<Button>(R.id.btnVolverDetalle).setOnClickListener {
            finish()
        }

        // Permanecen deshabilitadas hasta implementar su lógica
        findViewById<Button>(R.id.btnMarcarListo).isEnabled = false
        findViewById<Button>(R.id.btnEntregarPedido).isEnabled = false
        // Crear el Presentador MVP
        presentador = clsDetallePedidoPresentador(this, lifecycleScope)
        // Consultar el pedido real
        if (idPedido > 0) { presentador.cargarDetallePedido(idPedido)
        } else {
            mostrarError("No se recibió un pedido válido")
        }
    }

    // Mostrar mensaje mientras se consulta la API
    override fun mostrarCarga() {
        txtMensajeDetalle.text = "Consultando el detalle del pedido..."
    }

    // Recibir la información real del pedido
    override fun mostrarDetallePedido(pedido: dataDetallePedido) {
        findViewById<TextView>(R.id.txtFolioDetalle).text = "Folio: ${pedido.folio}"
        // Mostrar código de recogida
        findViewById<TextView>(R.id.txtCodigoRecogidaDetalle).text = "Código de recogida: ${pedido.codigo_recogida}"
        findViewById<TextView>(R.id.txtClienteDetalle).text = "Cliente: ${pedido.cliente}"
        // Mostrar fecha
        findViewById<TextView>(R.id.txtFechaDetalle).text = "Fecha: ${pedido.fecha}"
        // Mostrar estado del pedido
        val estado = when (pedido.estado) {
            "pendiente" -> "Pendiente"
            "listo" -> "Listo"
            "entregado" -> "Entregado"
            "cancelado" -> "Cancelado"
            else -> pedido.estado
        }
        findViewById<TextView>(R.id.txtEstadoDetalle).text = "Estado: $estado"
        // Mostrar estado del pago
        val estadoPago = when (pedido.estado_pago) {
            "por_cobrar" -> "Por cobrar"
            "pagado" -> "Pagado"
            "sin_cobro" -> "Sin cobro"
            else -> pedido.estado_pago
        }
        findViewById<TextView>(R.id.txtEstadoPagoDetalle).text = "Estado del pago: $estadoPago"
        // Mostrar notas
        findViewById<TextView>(R.id.txtNotasDetalle).text = pedido.notas?.takeIf { it.isNotBlank() } ?: "Sin observaciones"

        // Mostrar total
        findViewById<TextView>(R.id.txtTotalDetalle).text = formatoPrecio(pedido.total)
        // Mostrar los productos de la API
        mostrarProductos(pedido)

        // Confirmar que la información se recibió
        txtMensajeDetalle.text = "Detalle del pedido cargado correctamente"
    }

    // Mostrar los productos recibidos desde MySQL por la API
    private fun mostrarProductos(pedido: dataDetallePedido) {

        // Limpiar el contenedor antes de mostrar productos
        contenedorProductos.removeAllViews()

        if (pedido.productos.isEmpty()) {

            val mensaje = TextView(this)
            mensaje.text = "No hay productos registrados"
            mensaje.textSize = 14f
            mensaje.setTextColor(Color.GRAY)

            contenedorProductos.addView(mensaje)
            return
        }

        for (producto in pedido.productos) {

            // Nombre y cantidad
            val txtProducto = TextView(this)

            txtProducto.text =
                "${producto.cantidad} x ${producto.nombre}"

            txtProducto.textSize = 16f

            txtProducto.setTextColor(
                Color.parseColor("#503627")
            )

            txtProducto.setPadding(0, 12, 0, 4)

            contenedorProductos.addView(txtProducto)

            // Precio unitario y subtotal
            val txtPrecio = TextView(this)

            txtPrecio.text = "Precio: ${formatoPrecio(producto.precio_unitario)}" + "  |  Subtotal: ${formatoPrecio(producto.subtotal)}"

            txtPrecio.textSize = 13f

            txtPrecio.setTextColor(Color.parseColor("#746255"))

            txtPrecio.setPadding(0, 0, 0, 12)

            contenedorProductos.addView(txtPrecio)
        }
    }

    // Mostrar precios en pesos mexicanos
    private fun formatoPrecio(cantidad: Double): String {
        return String.format(Locale.forLanguageTag("es-MX"), "$%.2f", cantidad)
    }

    // Manejar errores de la API
    override fun mostrarError(mensaje: String) {

        txtMensajeDetalle.text = mensaje

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    // Avisar si la sesión caducó
    override fun solicitarLogin() {
        Toast.makeText(this, "Debes iniciar sesión nuevamente", Toast.LENGTH_LONG).show()
        finish()
    }
}