package com.example.minutanutricional

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * CRUD de recetas contra Firebase Realtime Database, nodo "recetas".
 *
 * Reemplaza a la lista fija RecetasRepository.recetasSemanales como fuente
 * de datos real para las vistas Minuta, Receta y BuscarReceta. La lista de
 * RecetasRepository se mantiene solo como dato semilla (ver
 * sembrarSiEsNecesario).
 */
object FirebaseRecetaRepository {

    private val recetasRef by lazy {
        FirebaseDatabase.getInstance().getReference("recetas")
    }

    /** READ: trae todas las recetas guardadas en Firebase. */
    suspend fun obtenerRecetas(): List<Receta> {
        val snapshot = recetasRef.get().await()
        return snapshot.children.mapNotNull { it.getValue(Receta::class.java) }
    }

    /** CREATE: agrega una receta nueva, generando su llave con push(). */
    suspend fun agregarReceta(receta: Receta): Result<Receta> {
        return try {
            val key = recetasRef.push().key
                ?: return Result.failure(IllegalStateException("No se pudo generar un id para la receta."))
            val recetaConId = receta.copy(id = key)
            recetasRef.child(key).setValue(recetaConId).await()
            Result.success(recetaConId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** UPDATE: sobrescribe una receta existente (usa receta.id como llave). */
    suspend fun actualizarReceta(receta: Receta): Result<Unit> {
        if (receta.id.isBlank()) {
            return Result.failure(IllegalArgumentException("La receta no tiene id."))
        }
        return try {
            recetasRef.child(receta.id).setValue(receta).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** DELETE: elimina una receta por su id. */
    suspend fun eliminarReceta(id: String): Result<Unit> {
        return try {
            recetasRef.child(id).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Si el nodo "recetas" está vacío (por ejemplo, la primera vez que se
     * conecta la app a un proyecto Firebase recién creado), sube las
     * recetas de ejemplo de RecetasRepository para que la base no quede sin
     * contenido. Si ya hay datos, no hace nada.
     */
    suspend fun sembrarSiEsNecesario() {
        val snapshot = recetasRef.get().await()
        if (snapshot.exists() && snapshot.childrenCount > 0L) return

        RecetasRepository.recetasSemanales.forEach { receta ->
            recetasRef.child(receta.id).setValue(receta).await()
        }
    }
}
