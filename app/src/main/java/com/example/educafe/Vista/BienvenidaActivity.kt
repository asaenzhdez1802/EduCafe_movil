package com.example.educafe.Vista

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Presentador.NavegacionSesion
import com.example.educafe.R
import com.google.android.material.button.MaterialButton

class BienvenidaActivity : AppCompatActivity() {

    private lateinit var btnIniciarSesion: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Al abrir desde el icono, revisar el rol guardado.
        if (redirigirAlPanel()) {
            return
        }

        // 2. Mostrar la bienvenida.
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

        // 3. Abrir Login únicamente cuando no exista sesión.
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)

        btnIniciarSesion.setOnClickListener {
            if (!SesionUsuario.haySesion()) {
                startActivity(
                    Intent(this, clsLoginVista::class.java)
                )
            }
        }

        // 4. Abrir el menú conservando la sesión.
        findViewById<MaterialButton>(R.id.btnVerMenu)
            .setOnClickListener {

                val ventana = Intent(
                    this,
                    MenuActivity::class.java
                ).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                }

                startActivity(ventana)
            }
    }

    // 5. Actualizar el botón al regresar a Bienvenida.
    override fun onResume() {
        super.onResume()

        // Si redirigimos al panel, los controles no se inicializan.
        if (!::btnIniciarSesion.isInitialized) {
            return
        }

        btnIniciarSesion.visibility =
            if (SesionUsuario.haySesion()) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }

    // 6. Recuperar el destino del administrador o cajero.
    private fun redirigirAlPanel(): Boolean {

        val abiertaDesdeIcono =
            intent.action == Intent.ACTION_MAIN &&
                    intent.hasCategory(Intent.CATEGORY_LAUNCHER)

        // Conservar el acceso voluntario a Inicio desde otras pantallas.
        if (!abiertaDesdeIcono) {
            return false
        }

        val pantalla = when (
            NavegacionSesion.destinoDespuesDelLogin()
        ) {
            NavegacionSesion.Destino.VENTAS_ADMIN ->
                VentasAdminActivity::class.java

            NavegacionSesion.Destino.RESUMEN_CAJERO ->
                ResumenCajeroActivity::class.java

            else -> return false
        }

        val ventana = Intent(this, pantalla).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(ventana)
        finish()

        return true
    }
}