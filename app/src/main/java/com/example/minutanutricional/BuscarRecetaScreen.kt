package com.example.minutanutricional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Vista "Buscar receta" pedida en los requerimientos generales de la
 * actividad (CRUD/funciones para las views Minuta, Receta y búsqueda).
 *
 * Permite filtrar, en tiempo real, las recetas obtenidas desde Firebase
 * Realtime Database por título, día o ingrediente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarRecetaScreen(
    onVolver: () -> Unit
) {
    var recetas by remember { mutableStateOf<List<Receta>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }
    var textoBusqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            recetas = FirebaseRecetaRepository.obtenerRecetas()
        } catch (e: Exception) {
            mensajeError = "No se pudieron cargar las recetas: ${e.message}"
        } finally {
            cargando = false
        }
    }

    val resultados = if (textoBusqueda.isBlank()) {
        recetas
    } else {
        recetas.filter { receta ->
            receta.titulo.contains(textoBusqueda, ignoreCase = true) ||
                receta.dia.contains(textoBusqueda, ignoreCase = true) ||
                receta.ingredientes.any { it.contains(textoBusqueda, ignoreCase = true) }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            TextButton(onClick = onVolver) {
                Text("← Volver")
            }
            Text(
                text = "Buscar receta",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            label = { Text("Buscar por título, día o ingrediente") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            cargando -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            mensajeError.isNotEmpty() -> {
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            resultados.isEmpty() -> {
                Text(
                    text = "No se encontraron recetas para \"$textoBusqueda\".",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(resultados) { receta ->
                        RecetaCard(receta)
                    }
                }
            }
        }
    }
}
