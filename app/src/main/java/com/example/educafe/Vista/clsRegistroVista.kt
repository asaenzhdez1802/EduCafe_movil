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
import com.example.educafe.Modelo.clsRegistroRespuesta
import com.example.educafe.Presentador.clsRegistroPresentador

class clsRegistroVista : AppCompatActivity(),
    clsRegistroPresentador.VistaRegistro {

    // 1. Campos del formulario.
    private lateinit var txtNombreRegistro: EditText
    private lateinit var txtUsuarioRegistro: EditText
    private lateinit var txtCelularRegistro: EditText
    private lateinit var txtPasswordRegistro: EditText
    private lateinit var txtConfirmarPassword: EditText

    // 2. Botones.
    private lateinit var btnCrearCuenta: Button
    private lateinit var btnRegresarRegistro: TextView

    // 3. Presentador.
    private lateinit var presentador: clsRegistroPresentador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_registro)

        // 4. Conectar controles XML.
        txtNombreRegistro = findViewById(R.id.txtNombreRegistro)
        txtUsuarioRegistro = findViewById(R.id.txtUsuarioRegistro)
        txtCelularRegistro = findViewById(R.id.txtCelularRegistro)
        txtPasswordRegistro = findViewById(R.id.txtPasswordRegistro)
        txtConfirmarPassword = findViewById(R.id.txtConfirmarPassword)

        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)
        btnRegresarRegistro = findViewById(R.id.btnRegresarRegistro)

        // 5. Inicializar Presentador.
        presentador = clsRegistroPresentador(
            this,
            lifecycleScope
        )

        // 6. Regresar al Login.
        btnRegresarRegistro.setOnClickListener {
            finish()
        }

        // 7. Registrar cliente.
        btnCrearCuenta.setOnClickListener {

            val nombre = txtNombreRegistro.text.toString()
            val usuario = txtUsuarioRegistro.text.toString()
            val celular = txtCelularRegistro.text.toString()
            val password = txtPasswordRegistro.text.toString()
            val confirmarPassword =
                txtConfirmarPassword.text.toString()

            presentador.registrarCliente(
                nombre,
                usuario,
                celular,
                password,
                confirmarPassword
            )
        }
    }

    // 8. Controlar el estado de carga.
    override fun mostrarCarga(cargando: Boolean) {

        btnCrearCuenta.isEnabled = !cargando

        btnCrearCuenta.text = if (cargando) {
            "Creando cuenta..."
        } else {
            "Crear cuenta"
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

    // 10. Registro confirmado por el servidor.
    override fun registroCorrecto(datos: clsRegistroRespuesta) {

        // Abrir P5 con los datos reales.
        val ventana = Intent(
            this,
            clsCuentaCreadaVista::class.java
        )

        ventana.putExtra("nombre", datos.nombre)
        ventana.putExtra("usuario", datos.usuario)
        ventana.putExtra("celular", datos.celular)

        startActivity(ventana)

        // Cerrar P4 para evitar repetir el registro.
        finish()
    }
}
