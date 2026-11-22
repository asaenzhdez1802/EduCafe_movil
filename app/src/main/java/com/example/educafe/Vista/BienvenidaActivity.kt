package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.educafe.R
import com.google.android.material.button.MaterialButton

class BienvenidaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bienvenida)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
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

        // Abrir la pantalla de inicio de sesión.
        findViewById<MaterialButton>(R.id.btnIniciarSesion)
            .setOnClickListener {
                val intent = Intent(
                    this,
                    clsLoginVista::class.java
                )
                startActivity(intent)
            }
        // Abrir el menú sin borrar la sesión.
        findViewById<MaterialButton>(R.id.btnVerMenu)
            .setOnClickListener {
                val intent = Intent(this, MenuActivity::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
                startActivity(intent)
            }
    }
}