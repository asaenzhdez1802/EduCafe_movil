
package com.example.educafe.Modelo

class clsValidacionesAcceso {

    fun validarLogin(
        usuario: String,
        password: String
    ): String {

        if (usuario.isBlank()) {
            return "Ingresa tu usuario"
        }

        if (password.isBlank()) {
            return "Ingresa tu contraseña"
        }

        return ""
    }

    fun validarRegistro(
        nombre: String,
        usuario: String,
        celular: String,
        password: String,
        confirmarPassword: String
    ): String {

        if (nombre.isBlank()) {
            return "Ingresa tu nombre"
        }

        if (!usuario.matches(Regex("[a-z0-9._]{4,30}"))) {
            return "El usuario debe tener de 4 a 30 caracteres válidos"
        }

        if (!celular.matches(Regex("[0-9]{10}"))) {
            return "El celular debe tener 10 dígitos"
        }

        if (password.length < 8) {
            return "La contraseña debe tener al menos 8 caracteres"
        }

        if (password != confirmarPassword) {
            return "Las contraseñas no coinciden"
        }

        return ""
    }
}
