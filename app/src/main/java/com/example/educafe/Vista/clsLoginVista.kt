
package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educafe.R
import com.example.educafe.Presentador.NavegacionSesion
import com.example.educafe.Presentador.clsLoginPresentador

class clsLoginVista : AppCompatActivity(),
    clsLoginPresentador.VistaLogin {

    private lateinit var txtUsuario: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var btnRegistrarse: Button
    private lateinit var btnRegresar: TextView

    private lateinit var presentador: clsLoginPresentador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // 1. Conectar los controles XML.
        txtUsuario = findViewById(R.id.txtUsuario)
        txtPassword = findViewById(R.id.txtPassword)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        btnRegresar = findViewById(R.id.btnRegresar)

        // 2. Crear el Presentador.
        presentador = clsLoginPresentador(
            this,
            lifecycleScope
        )

        // 3. Iniciar sesión.
        btnIniciarSesion.setOnClickListener {
            val usuario = txtUsuario.text.toString().trim()
            val password = txtPassword.text.toString()

            presentador.iniciarSesion(usuario, password)
        }

        // 4. Abrir P4 · Registro.
        btnRegistrarse.setOnClickListener {
            val ventana = Intent(
                this,
                clsRegistroVista::class.java
            )
            startActivity(ventana)
        }

        // 5. Regresar sin cerrar sesión.
        btnRegresar.setOnClickListener {
            finish()
        }
    }

    override fun mostrarCarga(cargando: Boolean) {
        btnIniciarSesion.isEnabled = !cargando

        btnIniciarSesion.text = if (cargando) {
            "Iniciando sesión..."
        } else {
            "Iniciar Sesión"
        }
    }

    override fun mostrarError(mensaje: String) {
        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun loginCorrecto(
        destino: NavegacionSesion.Destino
    ) {
        when (destino) {

            NavegacionSesion.Destino.MENU -> {
                mostrarMensajeDestino("Menú del cliente")
            }

            NavegacionSesion.Destino.RESUMEN_CAJERO -> {
                mostrarMensajeDestino("Resumen del cajero")
            }

            NavegacionSesion.Destino.VENTAS_ADMIN -> {
                mostrarMensajeDestino("Panel del administrador")
            }

            else -> {
                mostrarError(
                    "No se pudo determinar el destino"
                )
            }
        }
    }

    private fun mostrarMensajeDestino(pantalla: String) {
        Toast.makeText(
            this,
            "Acceso correcto: $pantalla",
            Toast.LENGTH_LONG
        ).show()
    }
}
