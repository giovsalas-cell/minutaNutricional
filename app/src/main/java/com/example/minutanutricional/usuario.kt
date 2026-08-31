package com.example.minutanutricional

/**
 * Representa a un usuario registrado en la aplicación.
 */
data class Usuario(
    val nombre: String,
    val email: String,
    val password: String,
    val tipoMinuta: String
)

/**
 * Repositorio simple en memoria para almacenar y validar usuarios registrados.
 * Al ser un `object`, sus datos se mantienen mientras la app esté en ejecución
 * (equivalente a una "base de datos" en memoria para efectos de esta actividad).
 *
 * Incluye 5 usuarios de prueba precargados para poder iniciar sesión sin
 * necesidad de registrarse primero.
 */
object UsuarioRepository {

    private val usuariosRegistrados = mutableListOf(
        Usuario(
            nombre = "Ana Torres",
            email = "ana@correo.com",
            password = "1234",
            tipoMinuta = "Estándar / Familiar"
        ),
        Usuario(
            nombre = "Carlos Muñoz",
            email = "carlos@correo.com",
            password = "1234",
            tipoMinuta = "Vegetariana"
        ),
        Usuario(
            nombre = "María Pérez",
            email = "maria@correo.com",
            password = "1234",
            tipoMinuta = "Hipocalórica"
        ),
        Usuario(
            nombre = "José Rojas",
            email = "jose@correo.com",
            password = "1234",
            tipoMinuta = "Estándar / Familiar"
        ),
        Usuario(
            nombre = "Valentina Soto",
            email = "valentina@correo.com",
            password = "1234",
            tipoMinuta = "Vegetariana"
        )
    )

    /**
     * Registra un nuevo usuario si el correo no existe previamente.
     * Retorna true si el registro fue exitoso, false si ya existía.
     */
    fun registrarUsuario(usuario: Usuario): Boolean {
        if (existeUsuario(usuario.email)) return false
        usuariosRegistrados.add(usuario)
        return true
    }

    /**
     * Verifica si ya existe un usuario registrado con ese correo.
     */
    fun existeUsuario(email: String): Boolean =
        usuariosRegistrados.any { it.email.equals(email.trim(), ignoreCase = true) }

    /**
     * Valida credenciales de acceso. Retorna el Usuario si son correctas,
     * o null si el correo no existe o la contraseña no coincide.
     */
    fun validarCredenciales(email: String, password: String): Usuario? =
        usuariosRegistrados.find {
            it.email.equals(email.trim(), ignoreCase = true) && it.password == password.trim()
        }

    /**
     * Obtiene un usuario registrado a partir de su correo.
     */
    fun obtenerUsuario(email: String): Usuario? =
        usuariosRegistrados.find { it.email.equals(email.trim(), ignoreCase = true) }

    /**
     * Lista de solo lectura de todos los usuarios registrados (útil para debug
     * o para mostrar en pantalla si se desea).
     */
    fun listarUsuarios(): List<Usuario> = usuariosRegistrados.toList()
}