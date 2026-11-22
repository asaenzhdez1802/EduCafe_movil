package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.educafe.R

class clsCuentaCreadaVista : AppCompatActivity() {

    // 1. Controles de la pantalla P5.
    private lateinit var txtNombreCreado: TextView
    private lateinit var txtUsuarioCreado: TextView
    private lateinit var txtCelularCreado: TextView
    private lateinit var btnIrLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Mostrar el diseño XML.
        setContentView(R.layout.activity_cuenta_creada)

        // 3. Conectar los controles.
        txtNombreCreado = findViewById(R.id.txtNombreCreado)
        txtUsuarioCreado = findViewById(R.id.txtUsuarioCreado)
        txtCelularCreado = findViewById(R.id.txtCelularCreado)
        btnIrLogin = findViewById(R.id.btnIrLogin)

        // 4. Recibir los datos enviados desde P4.
        val nombre = intent.getStringExtra("nombre")
        val usuario = intent.getStringExtra("usuario")
        val celular = intent.getStringExtra("celular")

        // 5. Mostrar los datos recibidos.
        txtNombreCreado.text = nombre ?: "Sin nombre"
        txtUsuarioCreado.text = usuario ?: "Sin usuario"
        txtCelularCreado.text = celular ?: "Sin celular"

        // 6. Volver a P3 · Login.
        btnIrLogin.setOnClickListener {

            val ventana = Intent(
                this,
                clsLoginVista::class.java
            )

            ventana.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )

            startActivity(ventana)
            finish()
        }
    }
}
