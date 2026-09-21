package com.example.minutanutricional

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

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
 * Repositorio de usuarios conectado al Content Provider y SQLite.
 */
object UsuarioRepository {

    private fun columnas() = arrayOf(
        UsuarioDbHelper.COL_NOMBRE,
        UsuarioDbHelper.COL_EMAIL,
        UsuarioDbHelper.COL_PASSWORD,
        UsuarioDbHelper.COL_TIPO_MINUTA
    )

    private fun cursorAUsuario(cursor: Cursor): Usuario {
        return Usuario(
            nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
            email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
            password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
            tipoMinuta = cursor.getString(cursor.getColumnIndexOrThrow("tipo_minuta"))
        )
    }
    fun registrarUsuario(context: Context, usuario: Usuario): Boolean {
        if (existeUsuario(context, usuario.email)) return false

        val values = ContentValues().apply {
            put(UsuarioDbHelper.COL_NOMBRE, usuario.nombre)
            put(UsuarioDbHelper.COL_EMAIL, usuario.email.trim())
            put(UsuarioDbHelper.COL_PASSWORD, usuario.password)
            put(UsuarioDbHelper.COL_TIPO_MINUTA, usuario.tipoMinuta)
        }

        val uri = context.contentResolver.insert(UsuarioContentProvider.CONTENT_URI, values)
        return uri != null
    }

    fun existeUsuario(context: Context, email: String): Boolean {
        val cursor = context.contentResolver.query(
            UsuarioContentProvider.CONTENT_URI,
            columnas(),
            "${UsuarioDbHelper.COL_EMAIL} = ? COLLATE NOCASE",
            arrayOf(email.trim()),
            null
        )
        return cursor?.use { it.count > 0 } ?: false
    }

    fun validarCredenciales(context: Context, email: String, password: String): Usuario? {
        val cursor = context.contentResolver.query(
            UsuarioContentProvider.CONTENT_URI,
            columnas(),
            "${UsuarioDbHelper.COL_EMAIL} = ? COLLATE NOCASE AND ${UsuarioDbHelper.COL_PASSWORD} = ?",
            arrayOf(email.trim(), password.trim()),
            null
        )
        return cursor?.use { if (it.moveToFirst()) cursorAUsuario(it) else null }
    }

    fun obtenerUsuario(context: Context, email: String): Usuario? {
        val cursor = context.contentResolver.query(
            UsuarioContentProvider.CONTENT_URI,
            columnas(),
            "${UsuarioDbHelper.COL_EMAIL} = ? COLLATE NOCASE",
            arrayOf(email.trim()),
            null
        )
        return cursor?.use { if (it.moveToFirst()) cursorAUsuario(it) else null }
    }

    fun actualizarPassword(context: Context, email: String, nuevaPassword: String): Boolean {
        val values = ContentValues().apply {
            put(UsuarioDbHelper.COL_PASSWORD, nuevaPassword.trim())
        }
        val filas = context.contentResolver.update(
            UsuarioContentProvider.CONTENT_URI,
            values,
            "${UsuarioDbHelper.COL_EMAIL} = ? COLLATE NOCASE",
            arrayOf(email.trim())
        )
        return filas > 0
    }

    fun listarUsuarios(context: Context): List<Usuario> {
        val cursor = context.contentResolver.query(
            UsuarioContentProvider.CONTENT_URI,
            columnas(),
            null,
            null,
            null
        )
        val resultado = mutableListOf<Usuario>()
        cursor?.use {
            while (it.moveToNext()) {
                resultado.add(cursorAUsuario(it))
            }
        }
        return resultado
    }
}