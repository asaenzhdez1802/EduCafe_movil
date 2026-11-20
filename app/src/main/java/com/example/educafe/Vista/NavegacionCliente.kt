package com.example.educafe.Vista

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.educafe.R

object NavegacionCliente {

    private const val PAQUETE = "com.example.educafe.Vista."

    const val PANTALLA_BIENVENIDA = PAQUETE + "BienvenidaActivity"
    const val PANTALLA_MENU = PAQUETE + "MenuActivity"
    const val PANTALLA_LOGIN = PAQUETE + "LoginActivity"
    const val PANTALLA_MI_PANEL = PAQUETE + "MiPanelActivity"
    const val PANTALLA_MIS_PEDIDOS = PAQUETE + "MisPedidosActivity"


    const val EXTRA_AVISO_PAGO = "AVISO_INICIA_SESION_PARA_PAGAR"

    const val INICIO = 0
    const val MENU = 1
    const val PEDIDOS = 2
    const val MI_PANEL = 3

    fun abrir(activity: Activity, pantalla: String, banderas: Int = 0, avisoPago: Boolean = false): Boolean {
        val intent = Intent()
        intent.setClassName(activity, pantalla)
        intent.addFlags(banderas)
        if (avisoPago) intent.putExtra(EXTRA_AVISO_PAGO, true)
        return try {
            activity.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, "Esa pantalla todavía no está integrada", Toast.LENGTH_SHORT).show()
            false
        }
    }


    fun irAlMenu(activity: Activity): Boolean {
        return abrir(activity, PANTALLA_MENU, Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }


    fun irALoginParaPagar(activity: Activity) {
        abrir(activity, PANTALLA_LOGIN, avisoPago = true)
    }

    fun irAMisPedidosTrasPedido(activity: Activity) {
        val menu = Intent().setClassName(activity, PANTALLA_MENU)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val misPedidos = Intent().setClassName(activity, PANTALLA_MIS_PEDIDOS)
        try {
            activity.startActivities(arrayOf(menu, misPedidos))
        } catch (e: ActivityNotFoundException) {
            // Si P2 aún no está integrada, solo se abre C5
            abrir(activity, PANTALLA_MIS_PEDIDOS)
        }
    }


    fun configurarBarra(activity: Activity, activa: Int) {
        val pestanas = listOf(
            Triple(R.id.tabInicio, R.id.imgTabInicio, R.id.txtTabInicio),
            Triple(R.id.tabMenu, R.id.imgTabMenu, R.id.txtTabMenu),
            Triple(R.id.tabPedidos, R.id.imgTabPedidos, R.id.txtTabPedidos),
            Triple(R.id.tabMiPanel, R.id.imgTabMiPanel, R.id.txtTabMiPanel)
        )

        val naranja = ContextCompat.getColor(activity, R.color.ic_naranja)
        val gris = ContextCompat.getColor(activity, R.color.ic_cafe)

        for (i in pestanas.indices) {
            val icono = activity.findViewById<ImageView>(pestanas[i].second)
            val texto = activity.findViewById<TextView>(pestanas[i].third)
            val esActiva = i == activa
            icono.setColorFilter(if (esActiva) naranja else gris)
            texto.setTextColor(if (esActiva) naranja else gris)
            texto.setTypeface(null, if (esActiva) Typeface.BOLD else Typeface.NORMAL)
        }

        activity.findViewById<LinearLayout>(R.id.tabInicio).setOnClickListener {
            abrir(activity, PANTALLA_BIENVENIDA, Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.findViewById<LinearLayout>(R.id.tabMenu).setOnClickListener {
            irAlMenu(activity)
        }
        activity.findViewById<LinearLayout>(R.id.tabPedidos).setOnClickListener {
            if (activa != PEDIDOS) {
                if (SesionCliente.token(activity) == null) abrir(activity, PANTALLA_LOGIN)
                else abrir(activity, PANTALLA_MIS_PEDIDOS)
            }
        }
        activity.findViewById<LinearLayout>(R.id.tabMiPanel).setOnClickListener {
            if (SesionCliente.token(activity) == null) abrir(activity, PANTALLA_LOGIN)
            else abrir(activity, PANTALLA_MI_PANEL)
        }
    }
}
