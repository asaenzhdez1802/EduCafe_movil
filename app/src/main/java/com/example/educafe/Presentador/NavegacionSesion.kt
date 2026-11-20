package com.example.educafe.Presentador

import com.example.educafe.Modelo.SesionUsuario

object NavegacionSesion {

    // 1. Destinos compartidos.
    enum class Destino {
        LOGIN,
        MENU,
        RESUMEN_CAJERO,
        VENTAS_ADMIN,
        PEDIDOS_CLIENTE,
        PANEL_CLIENTE
    }

    // 2. Destino después de iniciar sesión.
    fun destinoDespuesDelLogin(): Destino {

        if (!SesionUsuario.haySesion()) {
            return Destino.LOGIN
        }

        return when (SesionUsuario.obtenerRol()) {
            "cliente" -> Destino.MENU
            "cajero" -> Destino.RESUMEN_CAJERO
            "admin" -> Destino.VENTAS_ADMIN
            else -> Destino.LOGIN
        }
    }

    // 3. Destino del botón Pedidos del cliente.
    fun destinoPedidos(): Destino {

        if (!SesionUsuario.haySesion()) {
            return Destino.LOGIN
        }

        return if (SesionUsuario.obtenerRol() == "cliente") {
            Destino.PEDIDOS_CLIENTE
        } else {
            destinoDespuesDelLogin()
        }
    }

    // 4. Destino del botón Mi panel.
    fun destinoMiPanel(): Destino {

        if (!SesionUsuario.haySesion()) {
            return Destino.LOGIN
        }

        return if (SesionUsuario.obtenerRol() == "cliente") {
            Destino.PANEL_CLIENTE
        } else {
            destinoDespuesDelLogin()
        }
    }
}