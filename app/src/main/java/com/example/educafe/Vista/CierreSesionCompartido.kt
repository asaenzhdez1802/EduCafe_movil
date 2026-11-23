package com.example.educafe.Vista

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.educafe.Modelo.SesionUsuario
import com.example.educafe.Modelo.clsCarrito
import com.example.educafe.Presentador.clsCerrarSesionPresentador

object CierreSesionCompartido {

    fun conectar(
        activity: AppCompatActivity,
        boton: View
    ) {
        var tokenAlCerrar: String? = null

        val vista =
            object : clsCerrarSesionPresentador.VistaCerrarSesion {

                override fun mostrarCargaSalida(cargando: Boolean) {
                    if (!activity.isDestroyed) {
                        boton.isEnabled = !cargando
                    }
                }

                override fun mostrarErrorSalida(mensaje: String) {
                    if (!activity.isDestroyed) {
                        Toast.makeText(
                            activity,
                            mensaje,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun sesionCerrada() {
                    val tokenActual = SesionUsuario.obtenerToken()

                    // No afectar una sesión nueva.
                    if (
                        tokenActual != null &&
                        tokenActual != tokenAlCerrar
                    ) {
                        return
                    }

                    SesionUsuario.limpiarSesion()
                    clsCarrito.vaciar()

                    if (activity.isFinishing || activity.isDestroyed) {
                        return
                    }

                    val ventana = Intent(
                        activity,
                        BienvenidaActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                    activity.startActivity(ventana)
                    activity.finish()
                }
            }

        val presentador = clsCerrarSesionPresentador(
            vista,
            activity.lifecycleScope
        )

        boton.setOnClickListener {
            tokenAlCerrar = SesionUsuario.obtenerToken()
            presentador.cerrarSesion()
        }
    }
}