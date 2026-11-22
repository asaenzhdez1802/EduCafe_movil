package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educafe.R
import com.example.educafe.Modelo.clsRecompensasModelo
import com.example.educafe.Presentador.clsRecompensasPresentador

class clsRecompensasVista : AppCompatActivity(),
    clsRecompensasPresentador.VistaRecompensas {

    // 1. Datos de puntos y recompensas.
    private lateinit var txtPuntosRecompensas: TextView
    private lateinit var txtProximaRecompensa: TextView
    private lateinit var txtReglaPuntos: TextView
    private lateinit var txtEstadoRecompensas: TextView
    private lateinit var progresoPuntosRecompensas: ProgressBar
    private lateinit var contenedorRecompensas: LinearLayout

    // 2. Navegación.
    private lateinit var btnRegresarRecompensas: TextView
    private lateinit var navInicioRecompensas: TextView
    private lateinit var navMenuRecompensas: TextView
    private lateinit var navPedidosRecompensas: TextView
    private lateinit var navMiPanelRecompensas: TextView

    // 3. Presentador MVP.
    private lateinit var presentador: clsRecompensasPresentador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recompensas)

        conectarControles()
        configurarBotones()

        presentador = clsRecompensasPresentador(
            this,
            lifecycleScope
        )

        mostrarEstadoInicial()
        presentador.cargarRecompensas()
    }

    // 4. Conectar elementos del XML.
    private fun conectarControles() {

        txtPuntosRecompensas =
            findViewById(R.id.txtPuntosRecompensas)

        txtProximaRecompensa =
            findViewById(R.id.txtProximaRecompensa)

        txtReglaPuntos =
            findViewById(R.id.txtReglaPuntos)

        txtEstadoRecompensas =
            findViewById(R.id.txtEstadoRecompensas)

        progresoPuntosRecompensas =
            findViewById(R.id.progresoPuntosRecompensas)

        contenedorRecompensas =
            findViewById(R.id.contenedorRecompensas)

        btnRegresarRecompensas =
            findViewById(R.id.btnRegresarRecompensas)

        navInicioRecompensas =
            findViewById(R.id.navInicioRecompensas)

        navMenuRecompensas =
            findViewById(R.id.navMenuRecompensas)

        navPedidosRecompensas =
            findViewById(R.id.navPedidosRecompensas)

        navMiPanelRecompensas =
            findViewById(R.id.navMiPanelRecompensas)
    }

    // 5. Estado inicial.
    private fun mostrarEstadoInicial() {

        txtPuntosRecompensas.text = "— puntos"

        txtProximaRecompensa.text =
            "Consultando siguiente recompensa..."

        txtReglaPuntos.text =
            "Consultando regla de puntos..."

        txtEstadoRecompensas.visibility = View.VISIBLE
        txtEstadoRecompensas.text =
            "Cargando recompensas..."

        progresoPuntosRecompensas.progress = 0
        contenedorRecompensas.removeAllViews()
    }

    // 6. Estado de carga.
    override fun mostrarCarga(cargando: Boolean) {

        if (cargando) {
            mostrarEstadoInicial()
        }
    }

    // 7. Mostrar los datos de la API.
    override fun mostrarDatosRecompensas(
        datos: clsRecompensasModelo
    ) {

        val puntosCliente = datos.puntos

        txtPuntosRecompensas.text =
            if (puntosCliente != null) {
                "$puntosCliente puntos"
            } else {
                "Puntos no disponibles"
            }

        txtReglaPuntos.text = datos.regla

        // Limpiar las tarjetas anteriores.
        contenedorRecompensas.removeAllViews()

        // Mostrar las recompensas recibidas.
        for (recompensa in datos.recompensas) {

            agregarRecompensa(
                nombre = recompensa.nombre,
                descripcion = recompensa.descripcion,
                puntosRequeridos =
                    recompensa.puntos_requeridos,
                disponible = recompensa.alcanzable
            )
        }

        // Mostrar estado de la lista.
        if (datos.recompensas.isEmpty()) {

            txtEstadoRecompensas.visibility = View.VISIBLE
            txtEstadoRecompensas.text =
                "No hay recompensas disponibles por el momento"

        } else {
            txtEstadoRecompensas.visibility = View.GONE
        }

        // Buscar la siguiente recompensa no alcanzada.
        val siguiente = datos.recompensas
            .filter { !it.alcanzable }
            .minByOrNull { it.puntos_requeridos }

        if (siguiente != null && puntosCliente != null) {

            val requeridos =
                siguiente.puntos_requeridos

            val faltan =
                (requeridos - puntosCliente).coerceAtLeast(0)

            mostrarProgreso(
                siguiente.nombre,
                requeridos,
                faltan
            )

        } else if (siguiente != null) {

            txtProximaRecompensa.text =
                "Próxima recompensa: ${siguiente.nombre}"

            progresoPuntosRecompensas.progress = 0

        } else {

            txtProximaRecompensa.text =
                "No tienes recompensas pendientes por alcanzar"

            progresoPuntosRecompensas.progress = 0
        }
    }

    // 8. Calcular la barra de progreso.
    private fun mostrarProgreso(
        nombre: String,
        requeridos: Int,
        faltan: Int
    ) {

        txtProximaRecompensa.text =
            "Te faltan $faltan puntos para: $nombre"

        val porcentaje = if (requeridos > 0) {

            val acumulados =
                (requeridos - faltan)
                    .coerceIn(0, requeridos)

            ((acumulados.toLong() * 100) / requeridos)
                .toInt()
                .coerceIn(0, 100)

        } else {
            0
        }

        progresoPuntosRecompensas.progress = porcentaje
    }

    // 9. Agregar tarjetas dinámicamente.
    private fun agregarRecompensa(
        nombre: String,
        descripcion: String,
        puntosRequeridos: Int,
        disponible: Boolean
    ) {

        val tarjeta = LayoutInflater.from(this)
            .inflate(
                R.layout.item_recompensa,
                contenedorRecompensas,
                false
            )

        val txtNombre = tarjeta.findViewById<TextView>(
            R.id.txtNombreRecompensa
        )

        val txtPuntos = tarjeta.findViewById<TextView>(
            R.id.txtPuntosNecesarios
        )

        val txtDescripcion = tarjeta.findViewById<TextView>(
            R.id.txtDescripcionRecompensa
        )

        val txtDisponible = tarjeta.findViewById<TextView>(
            R.id.txtDisponibleRecompensa
        )

        val txtCobro = tarjeta.findViewById<TextView>(
            R.id.txtEstadoCobro
        )

        txtNombre.text = nombre
        txtPuntos.text = "$puntosRequeridos puntos"
        txtDescripcion.text = descripcion

        // La API indica si puede canjearse.
        txtDisponible.visibility =
            if (disponible) View.VISIBLE else View.GONE

        // No inventamos estados de cobro.
        txtCobro.visibility = View.GONE

        contenedorRecompensas.addView(tarjeta)
    }

    // 10. Errores.
    override fun mostrarErrorRecompensas(mensaje: String) {

        txtEstadoRecompensas.visibility = View.VISIBLE
        txtEstadoRecompensas.text = mensaje

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    // 11. Sesión vencida.
    override fun sesionVencidaRecompensas() {

        Toast.makeText(
            this,
            "Inicia sesión para consultar tus recompensas",
            Toast.LENGTH_LONG
        ).show()

        val ventana = Intent(
            this,
            clsLoginVista::class.java
        )

        ventana.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )

        startActivity(ventana)
        finish()
    }

    // 12. Configurar navegación.
    private fun configurarBotones() {

        btnRegresarRecompensas.setOnClickListener {
            finish()
        }

        navMiPanelRecompensas.setOnClickListener {

            val ventana = Intent(
                this,
                clsMiPanelVista::class.java
            )

            startActivity(ventana)
            finish()
        }

        navInicioRecompensas.setOnClickListener {
            mostrarPendiente("Inicio")
        }

        navMenuRecompensas.setOnClickListener {
            mostrarPendiente("Menú")
        }

        navPedidosRecompensas.setOnClickListener {
            mostrarPendiente("Pedidos")
        }
    }

    private fun mostrarPendiente(pantalla: String) {

        Toast.makeText(
            this,
            "Pendiente de conectar: $pantalla",
            Toast.LENGTH_SHORT
        ).show()
    }
}
