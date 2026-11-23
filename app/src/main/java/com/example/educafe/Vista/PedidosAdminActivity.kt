package com.example.educafe.Vista

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.R
import com.example.educafe.Adaptador.clsPedidosAdaptador
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.dataPedidos
import com.example.educafe.Presentador.VistaPedidos
import com.example.educafe.Presentador.clsPedidosPresentador
import com.google.android.material.button.MaterialButton

// A2: Pedidos del administrador
class PedidosAdminActivity :
    AppCompatActivity(), VistaPedidos {

    private lateinit var adaptador: clsPedidosAdaptador
    private lateinit var presentador: clsPedidosPresentador
    private lateinit var txtEstado: TextView

    private var todosPedidos: List<dataPedidos> = emptyList()
    private var filtroActual: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_pedidos_admin)

        // Respetar las barras del teléfono
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.raizPedidosAdmin)
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

        // Conectar los elementos del XML
        txtEstado = findViewById(R.id.txtEstadoPedidosAdmin)

        val listaPedidos =
            findViewById<RecyclerView>(R.id.rvPedidos)

        listaPedidos.layoutManager =
            LinearLayoutManager(this)

        // Reutilizar el adaptador del cajero
        adaptador = clsPedidosAdaptador(
            emptyList()
        ) { idPedido ->

            mostrarPedidoSeleccionado(idPedido)
        }

        listaPedidos.adapter = adaptador

        // Crear el presentador MVP
        presentador = clsPedidosPresentador(this)

        // Configurar filtros
        configurarFiltros()

        // Configurar barra del administrador
        configurarBarra()
    }

    override fun onResume() {
        super.onResume()

        // Verificar que exista una sesión
        if (!SesionUsuario.haySesion()) {
            solicitarLogin()
            return
        }

        // Verificar que sea administrador
        if (SesionUsuario.obtenerRol() != "admin") {

            Toast.makeText(
                this,
                "Acceso exclusivo para administradores",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        // Consultar pedidos al entrar o regresar
        if (::presentador.isInitialized) {
            presentador.cargarPedidos()
        }
    }

    // Configuración de filtros
    private fun configurarFiltros() {

        findViewById<Button>(R.id.btnTodos)
            .setOnClickListener {
                filtrarPedidos(null)
            }

        findViewById<Button>(R.id.btnPendientes)
            .setOnClickListener {
                filtrarPedidos("pendiente")
            }

        findViewById<Button>(R.id.btnListos)
            .setOnClickListener {
                filtrarPedidos("listo")
            }

        findViewById<Button>(R.id.btnEntregados)
            .setOnClickListener {
                filtrarPedidos("entregado")
            }
    }

    // Aplicar filtros a la lista
    private fun filtrarPedidos(estado: String?) {

        filtroActual = estado

        val filtrados = if (estado == null) {

            todosPedidos

        } else {

            todosPedidos.filter {
                it.estado.trim().equals(
                    estado,
                    ignoreCase = true
                )
            }
        }

        adaptador.actualizarPedidos(filtrados)

        // Actualizar mensaje
        txtEstado.text = if (filtrados.isEmpty()) {
            "No hay pedidos para este filtro"
        } else {
            "Mostrando ${filtrados.size} pedidos"
        }

        actualizarColoresFiltros()
    }

    // Resaltar el filtro seleccionado
    private fun actualizarColoresFiltros() {

        val botones = listOf(
            R.id.btnTodos to null,
            R.id.btnPendientes to "pendiente",
            R.id.btnListos to "listo",
            R.id.btnEntregados to "entregado"
        )

        botones.forEach { (id, estado) ->

            val boton = findViewById<Button>(id)
            val seleccionado = filtroActual == estado

            val fondo = ContextCompat.getColor(
                this,
                if (seleccionado) {
                    R.color.cafe_naranja
                } else {
                    R.color.cafe_blanco
                }
            )

            val texto = ContextCompat.getColor(
                this,
                if (seleccionado) {
                    R.color.cafe_blanco
                } else {
                    R.color.cafe_marron
                }
            )

            boton.backgroundTintList =
                ColorStateList.valueOf(fondo)

            boton.setTextColor(texto)
        }
    }

    // Navegación del administrador
    private fun configurarBarra() {

        val ventas = findViewById<MaterialButton>(
            R.id.navPedidosAdminVentas
        )

        val pedidos = findViewById<MaterialButton>(
            R.id.navPedidosAdminActual
        )

        val productos = findViewById<MaterialButton>(
            R.id.navPedidosAdminProductos
        )

        val stock = findViewById<MaterialButton>(
            R.id.navPedidosAdminStock
        )

        // Conservar los colores originales
        listOf(ventas, pedidos, productos, stock)
            .forEach { boton ->

                val color = ContextCompat.getColor(
                    this,
                    if (boton.id == R.id.navPedidosAdminActual) {
                        R.color.cafe_naranja
                    } else {
                        R.color.cafe_marron
                    }
                )

                boton.setTextColor(color)
                boton.iconTint = ColorStateList.valueOf(color)
            }

        // A1: Ventas generales
        ventas.setOnClickListener {
            startActivity(
                Intent(this, VentasAdminActivity::class.java)
            )
            finish()
        }

        // A2: Ya estamos en Pedidos
        pedidos.setOnClickListener {
            presentador.cargarPedidos()
        }

        // A3: Productos más vendidos
        productos.setOnClickListener {
            startActivity(
                Intent(this, MasVendidosActivity::class.java)
            )
            finish()
        }

        // A4: Stock
        stock.setOnClickListener {
            startActivity(
                Intent(this, StockAdminActivity::class.java)
            )
            finish()
        }
    }

    // Seleccionar un pedido
    private fun mostrarPedidoSeleccionado(idPedido: Int) {

        if (idPedido <= 0) {
            mostrarError("El pedido no es válido")
            return
        }

        // Consulta de solo lectura
        AlertDialog.Builder(this)
            .setTitle("Pedido #$idPedido")
            .setMessage(
                "Pedido seleccionado: $idPedido\n\n" +
                        "Consulta de solo lectura.\n" +
                        "El administrador no modifica el estado " +
                        "de los pedidos."
            )
            .setPositiveButton("Cerrar", null)
            .show()
    }

    // Mostrar el inicio de la consulta
    override fun mostrarCarga() {
        txtEstado.text = "Consultando pedidos..."
    }

    // Recibir los pedidos de la API
    override fun mostrarPedidos(pedidos: List<dataPedidos>) {

        todosPedidos = pedidos

        val pendientes = pedidos.count {
            it.estado.trim().equals(
                "pendiente",
                ignoreCase = true
            )
        }

        val listos = pedidos.count {
            it.estado.trim().equals(
                "listo",
                ignoreCase = true
            )
        }

        val entregados = pedidos.count {
            it.estado.trim().equals(
                "entregado",
                ignoreCase = true
            )
        }

        findViewById<TextView>(
            R.id.txtPendientes
        ).text = pendientes.toString()

        findViewById<TextView>(
            R.id.txtListos
        ).text = listos.toString()

        findViewById<TextView>(
            R.id.txtEntregados
        ).text = entregados.toString()

        // Mantener el filtro actual
        filtrarPedidos(filtroActual)
    }

    // Errores de la API
    override fun mostrarError(mensaje: String) {

        todosPedidos = emptyList()

        adaptador.actualizarPedidos(emptyList())

        txtEstado.text = mensaje

        findViewById<TextView>(
            R.id.txtPendientes
        ).text = "0"

        findViewById<TextView>(
            R.id.txtListos
        ).text = "0"

        findViewById<TextView>(
            R.id.txtEntregados
        ).text = "0"

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    // Sesión vencida
    override fun solicitarLogin() {

        Toast.makeText(
            this,
            "Debes iniciar sesión nuevamente",
            Toast.LENGTH_LONG
        ).show()

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
}