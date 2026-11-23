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
import com.example.educafe.Presentador.NavegacionSesion
import com.example.educafe.R

object NavegacionCliente {

    private const val PAQUETE = "com.example.educafe.Vista."

    const val PANTALLA_BIENVENIDA = PAQUETE + "BienvenidaActivity"
    const val PANTALLA_MENU = PAQUETE + "MenuActivity"
    const val PANTALLA_LOGIN = PAQUETE + "clsLoginVista"
    const val PANTALLA_MI_PANEL = PAQUETE + "clsMiPanelVista"
    const val PANTALLA_MIS_PEDIDOS = PAQUETE + "MisPedidosActivity"
    const val PANTALLA_RESUMEN_CAJERO = PAQUETE + "ResumenCajeroActivity"
    const val PANTALLA_VENTAS_ADMIN = PAQUETE + "VentasAdminActivity"


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


    fun abrirDestino(activity: Activity, destino: NavegacionSesion.Destino): Boolean {
        val pantalla = when (destino) {
            NavegacionSesion.Destino.LOGIN -> PANTALLA_LOGIN
            NavegacionSesion.Destino.MENU -> PANTALLA_MENU
            NavegacionSesion.Destino.RESUMEN_CAJERO -> PANTALLA_RESUMEN_CAJERO
            NavegacionSesion.Destino.VENTAS_ADMIN -> PANTALLA_VENTAS_ADMIN
            NavegacionSesion.Destino.PEDIDOS_CLIENTE -> PANTALLA_MIS_PEDIDOS
            NavegacionSesion.Destino.PANEL_CLIENTE -> PANTALLA_MI_PANEL
        }
        return abrir(activity, pantalla)
    }

    fun irAlMenu(activity: Activity): Boolean {
        return abrir(activity, PANTALLA_MENU, Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }



    fun sesionVencida(activity: Activity) {
        Toast.makeText(activity, "Tu sesión terminó. Inicia sesión nuevamente", Toast.LENGTH_LONG).show()
        abrir(activity, PANTALLA_LOGIN, Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.finish()
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
        val textoInactivo = ContextCompat.getColor(
            activity,
            R.color.cafe_texto_gris
        )

        for (i in pestanas.indices) {
            val icono = activity.findViewById<ImageView>(pestanas[i].second)
            val texto = activity.findViewById<TextView>(pestanas[i].third)
            val esActiva = i == activa
            icono.setColorFilter(if (esActiva) naranja else gris)
            texto.setTextColor(if (esActiva) naranja else textoInactivo)
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
                abrirDestino(activity, NavegacionSesion.destinoPedidos())
            }
        }
        activity.findViewById<LinearLayout>(R.id.tabMiPanel).setOnClickListener {
            abrirDestino(activity, NavegacionSesion.destinoMiPanel())
        }
    }

    fun navegarCompartido(activity: Activity, opcion: String) {
        when (opcion) {
            "Inicio" -> {
                abrir(
                    activity,
                    PANTALLA_BIENVENIDA,
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            }

            "Menú", "Hacer pedido" -> {
                irAlMenu(activity)
            }

            "Pedidos", "Mis pedidos" -> {
                abrirDestino(
                    activity,
                    NavegacionSesion.destinoPedidos()
                )
            }

            else -> {
                Toast.makeText(
                    activity,
                    "Destino no reconocido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
