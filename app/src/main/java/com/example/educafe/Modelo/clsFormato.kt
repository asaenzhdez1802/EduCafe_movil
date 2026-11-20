package com.example.educafe.Modelo

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


object clsFormato {

    private val zonaMexico = ZoneId.of("America/Mexico_City")
    private val formatoServidor = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val dias = arrayOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    private val meses = arrayOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")


    fun dinero(valor: Double, conMoneda: Boolean = true): String {
        val texto = String.format(Locale.US, "$%,.2f", valor)
        return if (conMoneda) "$texto MXN" else texto
    }

    private fun leer(fecha: String?): LocalDateTime? {
        if (fecha.isNullOrEmpty()) return null
        return try {
            LocalDateTime.parse(fecha, formatoServidor)
        } catch (e: Exception) {
            null
        }
    }

    fun hora(fecha: String?): String {
        val f = leer(fecha) ?: return ""
        val sufijo = if (f.hour < 12) "a. m." else "p. m."
        val hora12 = if (f.hour % 12 == 0) 12 else f.hour % 12
        return String.format(Locale.US, "%d:%02d %s", hora12, f.minute, sufijo)
    }


    // "2026-11-19 11:31:00" -> "11:31" (tarjetas de C5)
    fun horaCorta(fecha: String?): String {
        val f = leer(fecha) ?: return ""
        val hora12 = if (f.hour % 12 == 0) 12 else f.hour % 12
        return String.format(Locale.US, "%d:%02d", hora12, f.minute)
    }

    fun dia(fecha: String?): String {
        val f = leer(fecha) ?: return ""
        val hoy = LocalDate.now(zonaMexico)
        val d = f.toLocalDate()
        if (d == hoy) return "Hoy"
        if (d == hoy.minusDays(1)) return "Ayer"
        var texto = "${dias[d.dayOfWeek.value - 1]} ${d.dayOfMonth} ${meses[d.monthValue - 1]}"
        if (d.year != hoy.year) {
            texto += " ${d.year}"
        }
        return texto
    }


    fun diaYHora(fecha: String?): String {
        return "${dia(fecha)} · ${hora(fecha)}"
    }

    fun esReciente(fecha: String?): Boolean {
        val d = dia(fecha)
        return d == "Hoy" || d == "Ayer"
    }

    fun estado(estado: String): String {
        return when (estado) {
            "pendiente" -> "Pendiente"
            "listo" -> "Listo"
            "entregado" -> "Entregado"
            "cancelado" -> "Cancelado"
            else -> estado
        }
    }

    fun estadoPago(estadoPago: String): String {
        return when (estadoPago) {
            "por_cobrar" -> "Por cobrar"
            "pagado" -> "Pagado"
            "sin_cobro" -> "Sin cobro"
            else -> estadoPago
        }
    }

    fun motivoCancelacion(motivo: String?): String? {
        return if (motivo == "vencido") "no se pagó a tiempo" else null
    }
}
