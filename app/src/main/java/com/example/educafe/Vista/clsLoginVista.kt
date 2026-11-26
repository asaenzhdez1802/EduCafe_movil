package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
    private lateinit var btnVerPassword: ImageView

    private lateinit var presentador: clsLoginPresentador

    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // 1. Conectar los controles XML.
        txtUsuario = findViewById(R.id.txtUsuario)
        txtPassword = findViewById(R.id.txtPassword)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        btnRegresar = findViewById(R.id.btnRegresar)
        btnVerPassword = findViewById(R.id.btnVerPassword)

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

        // 6. Mostrar u ocultar contraseña.
        btnVerPassword.setOnClickListener {
            cambiarVisibilidadPassword()
        }
    }

    // Mostrar u ocultar la contraseña.
    private fun cambiarVisibilidadPassword() {

        val posicionInicio = txtPassword.selectionStart
        val posicionFin = txtPassword.selectionEnd

        passwordVisible = !passwordVisible

        if (passwordVisible) {

            // Mostrar contraseña.
            txtPassword.transformationMethod = null

            btnVerPassword.setImageResource(
                R.drawable.ic_ojo_cerrado
            )

            btnVerPassword.contentDescription =
                "Ocultar contraseña"

        } else {

            // Ocultar contraseña.
            txtPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()

            btnVerPassword.setImageResource(
                R.drawable.ic_ojo
            )

            btnVerPassword.contentDescription =
                "Mostrar contraseña"
        }

        // Conservar la posición del cursor.
        val longitud = txtPassword.text.length

        txtPassword.setSelection(
            posicionInicio.coerceIn(0, longitud),
            posicionFin.coerceIn(0, longitud)
        )
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

        val pantalla = when (destino) {

            NavegacionSesion.Destino.MENU ->
                MenuActivity::class.java

            NavegacionSesion.Destino.RESUMEN_CAJERO ->
                ResumenCajeroActivity::class.java

            NavegacionSesion.Destino.VENTAS_ADMIN ->
                VentasAdminActivity::class.java

            else -> {
                mostrarError("No se pudo determinar el destino")
                return
            }
        }

        val ventana = Intent(this, pantalla).apply {

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(ventana)
        finish()
    }
}
