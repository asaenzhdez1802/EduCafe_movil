package com.example.educafe.Adaptador

import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.educafe.R

object clsInsignias {

    private fun pintar(insignia: TextView, texto: String, colorFondo: Int, colorTexto: Int) {
        insignia.visibility = View.VISIBLE
        insignia.text = texto
        insignia.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(insignia.context, colorFondo))
        insignia.setTextColor(ContextCompat.getColor(insignia.context, colorTexto))
    }

    fun estado(insignia: TextView, estado: String) {
        when (estado) {
            "pendiente" -> pintar(insignia, "Pendiente", R.color.cafe_naranja_claro, R.color.cafe_naranja)
            "listo" -> pintar(insignia, "Listo", R.color.cafe_verde_claro, R.color.cafe_verde)
            "entregado" -> pintar(insignia, "Entregado", R.color.cafe_verde_claro, R.color.cafe_verde)
            "cancelado" -> pintar(insignia, "Cancelado", R.color.insignia_cancelado_fondo, R.color.insignia_cancelado_texto)
            else -> pintar(insignia, estado, R.color.cafe_crema_oscuro, R.color.cafe_texto_secundario)
        }
    }

    fun pago(insignia: TextView, estadoPago: String) {
        when (estadoPago) {
            "por_cobrar" -> pintar(insignia, "Por cobrar", R.color.insignia_por_cobrar_fondo, R.color.insignia_por_cobrar_texto)
            "pagado" -> pintar(insignia, "Pagado", R.color.insignia_pagado_fondo, R.color.insignia_pagado_texto)
            "sin_cobro" -> pintar(insignia, "Sin cobro", R.color.cafe_crema_oscuro, R.color.cafe_texto_secundario)
            else -> insignia.visibility = View.GONE
        }
    }

    fun noListo(insignia: TextView) {
        pintar(insignia, "No listo", R.color.cafe_crema_oscuro, R.color.cafe_texto_secundario)
    }
}