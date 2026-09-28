package com.example.minutanutricional

/**
 * Representa a un usuario registrado en la aplicación.
 *
 * NOTA: ya no se guarda la contraseña aquí. Firebase Authentication es quien
 * gestiona las credenciales de forma segura (hash + salt en sus servidores);
 * nosotros solo guardamos en Realtime Database los datos de perfil que
 * Firebase Auth no maneja (nombre y tipo de minuta preferido).
 */
data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val tipoMinuta: String = "Estándar / Familiar"
)
