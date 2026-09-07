package com.example.minutanutricional

/**
 * Funciones utilitarias de validación de datos de entrada.
 * Se centralizan aquí para poder reutilizarlas en Registro y,
 * si en el futuro se agrega, en edición de perfil.
 */
object Validaciones {

    // Patrón simple de correo: usuario@dominio.extensión
    private val patronEmail = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    // Al menos 6 caracteres, con al menos una letra y un número
    private val patronPassword = Regex("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")

    fun emailEsValido(email: String): Boolean = patronEmail.matches(email.trim())

    fun passwordEsValida(password: String): Boolean = patronPassword.matches(password)

    /**
     * Mensaje de ayuda a mostrar bajo el campo de contraseña,
     * explicando el requisito mínimo exigido.
     */
    const val AYUDA_PASSWORD = "Mínimo 6 caracteres, incluyendo al menos una letra y un número."

    const val AYUDA_EMAIL = "Ingresa un correo con formato válido (ej: nombre@dominio.com)."
}