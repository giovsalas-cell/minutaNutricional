package com.example.minutanutricional

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * Repositorio de usuarios respaldado por Firebase.
 *
 * - Autenticación (crear cuenta, iniciar sesión, recuperar contraseña) se
 *   delega completamente en Firebase Authentication.
 * - El perfil del usuario (nombre, tipo de minuta) se guarda en Firebase
 *   Realtime Database, en el nodo "usuarios/<uid>", usando como llave el uid
 *   que entrega Firebase Auth (así cada usuario solo puede leer/escribir su
 *   propio nodo, ver las reglas de seguridad en el README).
 *
 * Reemplaza a la antigua clase UsuarioRepository que trabajaba contra
 * SQLite/ContentProvider.
 */
object FirebaseUsuarioRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val usuariosRef by lazy {
        FirebaseDatabase.getInstance().getReference("usuarios")
    }

    /**
     * Registra un usuario nuevo: crea la cuenta en Firebase Auth y luego
     * guarda su perfil (nombre, tipoMinuta) en Realtime Database.
     *
     * Devuelve Result.success(Usuario) si todo salió bien, o
     * Result.failure(excepcion) con el motivo del error (correo ya usado,
     * contraseña débil, sin conexión, etc.) para mostrarlo en pantalla.
     */
    suspend fun registrarUsuario(
        nombre: String,
        email: String,
        password: String,
        tipoMinuta: String
    ): Result<Usuario> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(IllegalStateException("No se pudo crear el usuario."))

            val datosPerfil = mapOf(
                "nombre" to nombre,
                "email" to email.trim(),
                "tipoMinuta" to tipoMinuta
            )
            usuariosRef.child(uid).setValue(datosPerfil).await()

            Result.success(Usuario(uid = uid, nombre = nombre, email = email.trim(), tipoMinuta = tipoMinuta))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Valida credenciales contra Firebase Auth y, si son correctas, trae el
     * perfil del usuario desde Realtime Database.
     */
    suspend fun iniciarSesion(email: String, password: String): Result<Usuario> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(IllegalStateException("No se pudo iniciar sesión."))

            val snapshot = usuariosRef.child(uid).get().await()
            val nombre = snapshot.child("nombre").getValue(String::class.java) ?: ""
            val tipoMinuta = snapshot.child("tipoMinuta").getValue(String::class.java)
                ?: "Estándar / Familiar"

            Result.success(Usuario(uid = uid, nombre = nombre, email = email.trim(), tipoMinuta = tipoMinuta))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Envía un correo de recuperación de contraseña a través de Firebase
     * Auth. A diferencia de la versión anterior (que actualizaba la
     * contraseña directamente en la base local), Firebase no permite fijar
     * una contraseña nueva sin que el usuario esté autenticado o sin pasar
     * por el link que llega al correo, así que el flujo correcto es enviar
     * ese correo y que el usuario la cambie desde ahí.
     */
    suspend fun enviarCorreoDeRecuperacion(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Usuario actualmente autenticado (o null si no hay sesión activa). */
    fun usuarioActualUid(): String? = auth.currentUser?.uid

    fun cerrarSesion() {
        auth.signOut()
    }
}
