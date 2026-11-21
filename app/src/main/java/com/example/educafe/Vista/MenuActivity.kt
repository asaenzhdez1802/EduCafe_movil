package com.example.educafe.Vista

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.educafe.Adaptador.ProductosAdaptador
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.clsCarrito
import com.example.educafe.Modelo.dataCategorias
import com.example.educafe.Modelo.dataProductos
import com.example.educafe.Presentador.clsMenuPresentador
import com.example.educafe.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MenuActivity : AppCompatActivity() {

    private lateinit var presentador: clsMenuPresentador
    private lateinit var adaptador: ProductosAdaptador
    private lateinit var filtros: ChipGroup
    private lateinit var progreso: ProgressBar
    private lateinit var estado: TextView
    private lateinit var reintentar: MaterialButton

    private var idCategoria = 0
    private var cargando = false
    private var listaVacia = true
    private var errorActual: String? = null
    private var creandoFiltros = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        // 1. Respetar las barras del teléfono y el teclado.
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { vista, insets ->
            val barras = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            val teclado = insets.getInsets(
                WindowInsetsCompat.Type.ime()
            )

            vista.setPadding(
                barras.left,
                barras.top,
                barras.right,
                maxOf(barras.bottom, teclado.bottom)
            )
            insets
        }

        // 2. Recuperar controles y estado.
        idCategoria = savedInstanceState?.getInt("categoria") ?: 0

        filtros = findViewById(R.id.chipsCategorias)
        progreso = findViewById(R.id.progresoMenu)
        estado = findViewById(R.id.txtEstadoMenu)
        reintentar = findViewById(R.id.btnReintentarMenu)

        presentador = clsMenuPresentador(this)

        // 3. Mostrar productos en dos columnas.
        adaptador = ProductosAdaptador { producto ->
            agregarProducto(producto)
        }

        findViewById<RecyclerView>(R.id.rvProductos).apply {
            layoutManager = GridLayoutManager(this@MenuActivity, 2)
            adapter = adaptador
        }

        // 4. Buscar sobre los productos cargados.
        val buscar = findViewById<EditText>(R.id.edtBuscarProductos)
        buscar.setText(
            savedInstanceState?.getString("busqueda").orEmpty()
        )

        presentador.buscar(buscar.text.toString())

        buscar.doAfterTextChanged {
            presentador.buscar(it?.toString().orEmpty())
        }

        // 5. Reintentar la carga cuando falla.
        reintentar.setOnClickListener {
            presentador.cargarCategorias()
            presentador.cargarProductos(idCategoria)
        }

        // 6. Reutilizar la navegación compartida.
        NavegacionCliente.configurarBarra(
            this,
            NavegacionCliente.MENU
        )

        presentador.cargarCategorias()
    }

    override fun onResume() {
        super.onResume()

        // Actualizar disponibilidad al regresar al menú.
        presentador.cargarProductos(idCategoria)
    }

    fun mostrarCategorias(categorias: List<dataCategorias>) {
        creandoFiltros = true
        filtros.removeAllViews()

        val opciones = listOf(dataCategorias(0, "Todos")) + categorias
        val categoriaAnterior = idCategoria

        if (opciones.none { it.idCategoria == idCategoria }) {
            idCategoria = 0
        }

        for (opcion in opciones) {
            val chip = Chip(this).apply {
                id = View.generateViewId()
                text = opcion.nombre
                isCheckable = true
                isCheckedIconVisible = false

                chipBackgroundColor = colores(
                    R.color.cafe_naranja,
                    R.color.cafe_blanco
                )

                setTextColor(
                    colores(
                        R.color.cafe_blanco,
                        R.color.cafe_marron
                    )
                )

                chipStrokeColor = colores(
                    R.color.cafe_naranja,
                    R.color.cafe_borde_suave
                )
                chipStrokeWidth = resources.displayMetrics.density

                setOnCheckedChangeListener { _, seleccionado ->
                    if (
                        !creandoFiltros &&
                        seleccionado &&
                        idCategoria != opcion.idCategoria
                    ) {
                        idCategoria = opcion.idCategoria
                        presentador.cargarProductos(idCategoria)
                    }
                }
            }

            filtros.addView(chip)
            chip.isChecked = opcion.idCategoria == idCategoria
        }

        creandoFiltros = false

        if (categoriaAnterior != idCategoria) {
            presentador.cargarProductos(idCategoria)
        }
    }

    private fun colores(activo: Int, normal: Int): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
            ),
            intArrayOf(
                ContextCompat.getColor(this, activo),
                ContextCompat.getColor(this, normal)
            )
        )
    }

    fun mostrarProductos(productos: List<dataProductos>) {
        adaptador.actualizar(productos)
        listaVacia = productos.isEmpty()
        actualizarEstado()
    }

    fun mostrarCarga(cargando: Boolean) {
        this.cargando = cargando

        if (cargando) {
            errorActual = null
        }

        actualizarEstado()
    }

    fun mostrarError(mensaje: String) {
        errorActual = mensaje
        actualizarEstado()
    }

    private fun actualizarEstado() {
        progreso.visibility =
            if (cargando) View.VISIBLE else View.GONE

        estado.text =
            errorActual ?: getString(R.string.menu_sin_productos)

        estado.visibility =
            if (!cargando && (errorActual != null || listaVacia)) {
                View.VISIBLE
            } else {
                View.GONE
            }

        reintentar.visibility =
            if (!cargando && errorActual != null) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    fun mostrarAviso(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    fun solicitarLogin() {
        mostrarAviso("Inicia sesión para continuar")
        NavegacionCliente.abrir(
            this,
            NavegacionCliente.PANTALLA_LOGIN
        )
    }

    private fun agregarProducto(producto: dataProductos) {
        // El catálogo es público; agregar requiere sesión.
        if (!SesionUsuario.haySesion()) {
            solicitarLogin()
            return
        }

        if (SesionUsuario.obtenerRol() != "cliente") {
            mostrarAviso(
                "Los pedidos del menú son para cuentas de cliente"
            )
            return
        }

        // Reutilizar el carrito compartido de Angelino.
        val error = clsCarrito.agregar(
            producto.idProducto,
            producto.nombre,
            producto.precioVenta,
            producto.imagen,
            producto.disponibles
        )

        if (error != null) {
            mostrarAviso(error)
            return
        }

        val intent = Intent(this, TuPedidoActivity::class.java)
        intent.putExtra(TuPedidoActivity.PRODUCTO_AGREGADO, true)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("categoria", idCategoria)
        outState.putString(
            "busqueda",
            findViewById<EditText>(R.id.edtBuscarProductos)
                .text.toString()
        )

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        presentador.destruir()
        super.onDestroy()
    }
}