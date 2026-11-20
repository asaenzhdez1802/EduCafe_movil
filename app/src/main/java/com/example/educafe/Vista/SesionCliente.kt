package com.example.educafe.Vista

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.educafe.Modelo.clsCarrito
import com.example.educafe.Modelo.clsSesion


object SesionCliente {


    fun token(context: Context): String? {
        val token = clsSesion.obtenerToken(context)
        return if (token.isNullOrEmpty()) null else token
    }

    fun sesionVencida(activity: Activity) {
        Toast.makeText(activity, "Tu sesión terminó. Inicia sesión nuevamente", Toast.LENGTH_LONG).show()
        clsSesion.cerrarSesionLocal(activity)
        clsCarrito.vaciar()
        NavegacionCliente.abrir(
            activity,
            NavegacionCliente.PANTALLA_LOGIN,
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        activity.finish()
    }
}
