package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.Adaptador.clsPedidosAdaptador
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educafe.R
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.dataPedidos
import com.example.educafe.Modelo.dataResumenCajero
import com.example.educafe.Presentador.VistaResumenCajero
import com.example.educafe.Presentador.clsResumenCajeroPresentador
import java.text.NumberFormat
import java.util.Locale

// K1 - Resumen del cajero
class ResumenCajeroActivity :
    AppCompatActivity(), VistaResumenCajero {

    private lateinit var presentador: clsResumenCajeroPresentador
    private lateinit var adaptadorPedidos: clsPedidosAdaptador
    private lateinit var rvPedidosResumen: RecyclerView

    private lateinit var txtEstadoPedidos: TextView
    private lateinit var txtVentasHoy: TextView
    private lateinit var txtPorPreparar: TextView
    private lateinit var txtPedidosListos: TextView
    private lateinit var txtTicketPromedio: TextView

    private val formatoDinero =
        NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("es-MX")
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Validar sesión de cajero
        if (!SesionUsuario.haySesion() ||
            SesionUsuario.obtenerRol() != "cajero"
        ) {
            Toast.makeText(
                this,
                "Acceso exclusivo para cajeros",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        // Cargar el diseño
        setContentView(R.layout.activity_resumen_cajero)

        // Configurar la lista de pedidos activos del resumen.
        rvPedidosResumen = findViewById(R.id.rvPedidosResumen)
        rvPedidosResumen.layoutManager = LinearLayoutManager(this)
        rvPedidosResumen.isNestedScrollingEnabled = false

        adaptadorPedidos = clsPedidosAdaptador(emptyList()) { idPedido ->
            abrirDetallePedido(idPedido)
        }
        rvPedidosResumen.adapter = adaptadorPedidos

        // Conectar controles con XML
        txtEstadoPedidos =
            findViewById(R.id.txtEstadoPedidos)

        txtVentasHoy =
            findViewById(R.id.txtVentasHoy)

        txtPorPreparar =
            findViewById(R.id.txtPorPreparar)

        txtPedidosListos =
            findViewById(R.id.txtPedidosListos)

        txtTicketPromedio =
            findViewById(R.id.txtTicketPromedio)

        // Crear presentador
        presentador = clsResumenCajeroPresentador(
            this,
            lifecycleScope
        )

        // Abrir K2 - Pedidos
        findViewById<Button>(R.id.btnVerPedidos)
            .setOnClickListener {
                abrirPedidos()
            }

        findViewById<TextView>(R.id.navPedidosCajero)
            .setOnClickListener {
                abrirPedidos()
            }

        // Abrir K4 - Mis ventas
        findViewById<TextView>(R.id.navMisVentasCajero)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        MisVentasCajeroActivity::class.java
                    )
                )
            }


        CierreSesionCompartido.conectar(
            this,
            findViewById(R.id.btnCerrarSesion)
        )
    }

    // Recargar información al regresar
    override fun onResume() {
        super.onResume()

        if (::presentador.isInitialized) {
            presentador.cargarResumen()
        }
    }

    // Ir a la pantalla de pedidos
    private fun abrirPedidos() {

        startActivity(
            Intent(
                this,
                PedidosCajeroActivity::class.java
            )
        )
    }

    // Abrir el detalle del pedido seleccionado desde Resumen.
    private fun abrirDetallePedido(idPedido: Int) {
        if (idPedido <= 0) {
            mostrarAviso("El pedido seleccionado no es válido")
            return
        }

        val intent = Intent(this, DetallePedidoActivity::class.java)
        intent.putExtra("id_pedido", idPedido)
        startActivity(intent)
    }

    // Avisar que se está consultando el servidor
    override fun mostrarCarga() {

        txtEstadoPedidos.text =
            "Consultando información..."
    }

    // Mostrar únicamente pedidos activos: pendientes y listos.
    // Se excluyen entregados y cancelados.
    override fun mostrarPedidos(
        pedidos: List<dataPedidos>
    ) {
        val pedidosActivos = pedidos.filter { pedido ->
            val estado = pedido.estado.trim().lowercase(Locale.ROOT)
            estado == "pendiente" || estado == "listo"
        }

        val pendientes = pedidosActivos.count {
            it.estado.trim().equals("pendiente", ignoreCase = true)
        }

        val listos = pedidosActivos.count {
            it.estado.trim().equals("listo", ignoreCase = true)
        }

        txtPorPreparar.text = pendientes.toString()
        txtPedidosListos.text = listos.toString()

        adaptadorPedidos.actualizarPedidos(pedidosActivos)

        txtEstadoPedidos.text = if (pedidosActivos.isEmpty()) {
            "No hay pedidos activos"
        } else {
            "Mostrando ${pedidosActivos.size} pedidos activos"
        }
    }

    // Mostrar estadísticas reales de la API
    override fun mostrarEstadisticas(
        datos: dataResumenCajero
    ) {

        // Total cobrado hoy
        txtVentasHoy.text =
            formatoDinero.format(datos.total_vendido_hoy)

        // Ticket promedio
        txtTicketPromedio.text =
            formatoDinero.format(datos.ticket_promedio)

        // Pedidos pendientes
        txtPorPreparar.text =
            datos.pedidos_por_preparar.toString()

        // Pedidos listos
        txtPedidosListos.text =
            datos.pedidos_listos.toString()
    }

    // Mostrar errores
    override fun mostrarError(mensaje: String) {
        adaptadorPedidos.actualizarPedidos(emptyList())
        txtEstadoPedidos.text = mensaje
        mostrarAviso(mensaje)
    }

    // Sesión vencida
    override fun solicitarLogin() {

        mostrarAviso(
            "Tu sesión venció. Inicia sesión nuevamente"
        )

        val intent = Intent(
            this,
            clsLoginVista::class.java
        )

        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )

        startActivity(intent)
        finish()
    }

    // Mostrar avisos
    private fun mostrarAviso(mensaje: String) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }
}