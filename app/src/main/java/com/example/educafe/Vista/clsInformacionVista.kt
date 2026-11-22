package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educafe.R
import com.example.educafe.Modelo.clsInformacionModelo
import com.example.educafe.Presentador.clsInformacionPresentador

class clsInformacionVista : AppCompatActivity(),
    clsInformacionPresentador.VistaInformacion {

    // 1. Textos informativos.
    private lateinit var txtMisionInformacion: TextView
    private lateinit var txtVisionInformacion: TextView
    private lateinit var txtHorarioInformacion: TextView
    private lateinit var txtUbicacionInformacion: TextView
    private lateinit var txtContactoInformacion: TextView
    private lateinit var txtCorreoInformacion: TextView
    private lateinit var txtEstadoInformacion: TextView

    // 2. Botones de navegación.
    private lateinit var btnRegresarInformacion: TextView
    private lateinit var navInicioInformacion: TextView
    private lateinit var navMenuInformacion: TextView
    private lateinit var navPedidosInformacion: TextView
    private lateinit var navMiPanelInformacion: TextView

    // 3. Presentador MVP.
    private lateinit var presentador: clsInformacionPresentador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_informacion)

        conectarControles()
        configurarBotones()

        presentador = clsInformacionPresentador(
            this,
            lifecycleScope
        )

        presentador.cargarInformacion()
    }

    // 4. Conectar elementos del XML.
    private fun conectarControles() {

        txtMisionInformacion =
            findViewById(R.id.txtMisionInformacion)

        txtVisionInformacion =
            findViewById(R.id.txtVisionInformacion)

        txtHorarioInformacion =
            findViewById(R.id.txtHorarioInformacion)

        txtUbicacionInformacion =
            findViewById(R.id.txtUbicacionInformacion)

        txtContactoInformacion =
            findViewById(R.id.txtContactoInformacion)

        txtCorreoInformacion =
            findViewById(R.id.txtCorreoInformacion)

        txtEstadoInformacion =
            findViewById(R.id.txtEstadoInformacion)

        btnRegresarInformacion =
            findViewById(R.id.btnRegresarInformacion)

        navInicioInformacion =
            findViewById(R.id.navInicioInformacion)

        navMenuInformacion =
            findViewById(R.id.navMenuInformacion)

        navPedidosInformacion =
            findViewById(R.id.navPedidosInformacion)

        navMiPanelInformacion =
            findViewById(R.id.navMiPanelInformacion)
    }

    // 5. Mostrar estado de carga.
    override fun mostrarCargaInformacion(cargando: Boolean) {

        if (cargando) {

            txtMisionInformacion.text = "Cargando misión..."
            txtVisionInformacion.text = "Cargando visión..."
            txtHorarioInformacion.text = "Cargando horario..."
            txtUbicacionInformacion.text = "Cargando ubicación..."
            txtContactoInformacion.text = "Cargando teléfono..."
            txtCorreoInformacion.text = "Cargando correo..."

            txtEstadoInformacion.visibility = View.VISIBLE
            txtEstadoInformacion.text =
                "Consultando información de Edu Café..."
        }
    }

    // 6. Mostrar información real.
    override fun mostrarDatosInformacion(
        datos: clsInformacionModelo
    ) {

        txtMisionInformacion.text =
            textoDisponible(datos.mision)

        txtVisionInformacion.text =
            textoDisponible(datos.vision)

        txtHorarioInformacion.text =
            textoDisponible(datos.horario)

        txtUbicacionInformacion.text =
            textoDisponible(datos.direccion)

        txtContactoInformacion.text =
            textoDisponible(datos.telefono)

        txtCorreoInformacion.text =
            textoDisponible(datos.correo)

        txtEstadoInformacion.visibility = View.GONE
    }

    // 7. Evitar campos vacíos.
    private fun textoDisponible(valor: String?): String {

        return if (valor.isNullOrBlank()) {
            "Información no disponible"
        } else {
            valor
        }
    }

    // 8. Mostrar errores.
    override fun mostrarErrorInformacion(mensaje: String) {

        txtEstadoInformacion.visibility = View.VISIBLE
        txtEstadoInformacion.text = mensaje

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    // 9. Configurar botones.
    private fun configurarBotones() {

        btnRegresarInformacion.setOnClickListener {
            finish()
        }

        navMiPanelInformacion.setOnClickListener {

            val ventana = Intent(
                this,
                clsMiPanelVista::class.java
            )

            ventana.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )

            startActivity(ventana)
            finish()
        }

        navInicioInformacion.setOnClickListener {
            mostrarPendiente("Inicio")
        }

        navMenuInformacion.setOnClickListener {
            mostrarPendiente("Menú")
        }

        navPedidosInformacion.setOnClickListener {
            mostrarPendiente("Pedidos")
        }
    }

    // 10. Navegación pendiente de otros módulos.
    private fun mostrarPendiente(pantalla: String) {

        Toast.makeText(
            this,
            "Pendiente de conectar: $pantalla",
            Toast.LENGTH_SHORT
        ).show()
    }
}
