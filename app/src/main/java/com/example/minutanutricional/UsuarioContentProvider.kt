package com.example.minutanutricional



import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class UsuarioContentProvider : ContentProvider() {

    private lateinit var dbHelper: UsuarioDbHelper

    companion object {
        const val AUTHORITY = "com.example.minutanutricional.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/${UsuarioDbHelper.TABLE_USUARIOS}")
    }

    override fun onCreate(): Boolean {
        dbHelper = UsuarioDbHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            UsuarioDbHelper.TABLE_USUARIOS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val id = db.insert(UsuarioDbHelper.TABLE_USUARIOS, null, values)
        return if (id > 0) {
            context?.contentResolver?.notifyChange(uri, null)
            ContentUris.withAppendedId(CONTENT_URI, id)
        } else {
            null
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val filas = db.update(UsuarioDbHelper.TABLE_USUARIOS, values, selection, selectionArgs)
        if (filas > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return filas
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete(UsuarioDbHelper.TABLE_USUARIOS, selection, selectionArgs)
        if (filas > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return filas
    }

    override fun getType(uri: Uri): String {
        return "vnd.android.cursor.dir/vnd.${UsuarioDbHelper.TABLE_USUARIOS}"
    }
}