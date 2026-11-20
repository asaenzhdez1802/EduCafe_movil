package com.example.educafe

import android.app.Application
import com.example.educafe.Modelo.SesionUsuario

class EduCafeApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Inicializar la sesión compartida.
        SesionUsuario.inicializar(this)
    }
}