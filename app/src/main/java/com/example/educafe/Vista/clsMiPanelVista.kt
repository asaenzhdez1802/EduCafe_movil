package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educafe.R
import com.example.educafe.Modelo.clsPerfilModelo
import com.example.educafe.Presentador.clsMiPanelPresentador

class clsMiPanelVista : AppCompatActivity(),
    clsMiPanelPresentador.VistaMiPanel {

    // 1. Información del cliente.
    private lateinit var txtNombrePanel: TextView
    private lateinit var txtUsuarioPanel: TextView
    private lateinit var txtPuntosPanel: TextView

    // 2. Información de recompensas.
    private lateinit var txtSiguienteRecompensa: TextView
    private lateinit var txtDetallePuntos: TextView
    private lateinit var progresoRecompensa: ProgressBar

    // 3. Accesos del panel.
    private lateinit var btnHacerPedido: TextView
    private lateinit var btnMisRecompensas: TextView
    private lateinit var btnMisPedidos: TextView
    private lateinit var btnInformacion: TextView

    // 4. Navegación inferior.
    private lateinit var navInicio: TextView
    private lateinit var navMenu: TextView
    private lateinit var navPedidos: TextView
    private lateinit var navMiPanel: TextView

    // 5. Presentador.
    private lateinit var presentador: clsMiPanelPresentador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mi_panel)

        conectarControles()
        configurarBotones()

        presentador = clsMiPanelPresentador(
            this,
            lifecycleScope
        )

        presentador.cargarPerfil()
        CierreSesionCompartido.conectar(
            this,
            findViewById(R.id.btnCerrarSesionCompartida)
        )
    }

    // 6. Conectar XML con Kotlin.
    private fun conectarControles() {

        txtNombrePanel =
            findViewById(R.id.txtNombrePanel)

        txtUsuarioPanel =
            findViewById(R.id.txtUsuarioPanel)

        txtPuntosPanel =
            findViewById(R.id.txtPuntosPanel)

        txtSiguienteRecompensa =
            findViewById(R.id.txtSiguienteRecompensa)

        txtDetallePuntos =
            findViewById(R.id.txtDetallePuntos)

        progresoRecompensa =
            findViewById(R.id.progresoRecompensa)

        btnHacerPedido =
            findViewById(R.id.btnHacerPedido)

        btnMisRecompensas =
            findViewById(R.id.btnMisRecompensas)

        btnMisPedidos =
            findViewById(R.id.btnMisPedidos)

        btnInformacion =
            findViewById(R.id.btnInformacion)

        navInicio =
            findViewById(R.id.navInicio)

        navMenu =
            findViewById(R.id.navMenu)

        navPedidos =
            findViewById(R.id.navPedidos)

        navMiPanel =
            findViewById(R.id.navMiPanel)
    }

    // 7. Estado de carga.
    override fun mostrarCarga(cargando: Boolean) {

        if (cargando) {

            txtNombrePanel.text = "Cargando perfil..."
            txtUsuarioPanel.text = "Consultando..."
            txtPuntosPanel.text = "— puntos"

            txtSiguienteRecompensa.text =
                "Consultando siguiente recompensa..."

            txtDetallePuntos.text = "Cargando puntos..."

            progresoRecompensa.progress = 0
        }
    }

    // 8. Mostrar los datos del perfil.
    override fun mostrarPerfil(datos: clsPerfilModelo) {

        txtNombrePanel.text = datos.nombre
        txtUsuarioPanel.text = datos.usuario
        txtPuntosPanel.text = "${datos.puntos} puntos"

        val recompensa = datos.siguiente_recompensa

        if (recompensa != null) {

            txtSiguienteRecompensa.text =
                recompensa.nombre

            txtDetallePuntos.text =
                "Te faltan ${recompensa.faltan} puntos"

            val requeridos =
                recompensa.puntos_requeridos

            val acumulados =
                (requeridos - recompensa.faltan)
                    .coerceIn(
                        0,
                        requeridos.coerceAtLeast(0)
                    )

            val porcentaje = if (requeridos > 0) {

                ((acumulados.toLong() * 100) / requeridos)
                    .toInt()
                    .coerceIn(0, 100)

            } else {
                0
            }

            progresoRecompensa.progress = porcentaje

        } else {

            txtSiguienteRecompensa.text =
                "No hay una siguiente recompensa"

            txtDetallePuntos.text =
                "Consulta tus recompensas disponibles"

            progresoRecompensa.progress = 0
        }
    }

    // 9. Mostrar errores.
    override fun mostrarError(mensaje: String) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    // 10. Sesión vencida.
    override fun sesionVencida() {

        Toast.makeText(
            this,
            "Inicia sesión para consultar tu perfil",
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

    // 11. Configurar botones.
    private fun configurarBotones() {

        btnHacerPedido.setOnClickListener {
            NavegacionCliente.navegarCompartido(this, "Hacer pedido")
        }

        // Abrir C7.
        btnMisRecompensas.setOnClickListener {

            val ventana = Intent(
                this,
                clsRecompensasVista::class.java
            )

            startActivity(ventana)
        }

        btnMisPedidos.setOnClickListener {
            NavegacionCliente.navegarCompartido(this, "Mis pedidos")
        }

        // Abrir C8.
        btnInformacion.setOnClickListener {

            val ventana = Intent(
                this,
                clsInformacionVista::class.java
            )

            startActivity(ventana)
        }

        navInicio.setOnClickListener {
            NavegacionCliente.navegarCompartido(this, "Inicio")
        }

        navMenu.setOnClickListener {
            NavegacionCliente.navegarCompartido(this, "Menú")
        }

        navPedidos.setOnClickListener {
            NavegacionCliente.navegarCompartido(this, "Pedidos")
        }

        navMiPanel.setOnClickListener {
            // Ya estamos en Mi panel.
        }
    }


}
