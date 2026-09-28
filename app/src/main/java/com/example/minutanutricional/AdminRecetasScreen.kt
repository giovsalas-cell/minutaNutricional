package com.example.minutanutricional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRecetasScreen(
    onVolver: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var recetas by remember { mutableStateOf<List<Receta>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }
    var mensajeInfo by remember { mutableStateOf("") }

    var recetaEnEdicion by remember { mutableStateOf<Receta?>(null) }
    var mostrandoFormulario by remember { mutableStateOf(false) }

    suspend fun recargar() {
        cargando = true
        try {
            recetas = FirebaseRecetaRepository.obtenerRecetas()
            mensajeError = ""
        } catch (e: Exception) {
            mensajeError = "No se pudieron cargar las recetas: ${e.message}"
        } finally {
            cargando = false
        }
    }

    LaunchedEffect(Unit) { recargar() }

    if (mostrandoFormulario) {
        FormularioReceta(
            recetaInicial = recetaEnEdicion,
            onCancelar = { mostrandoFormulario = false },
            onGuardar = { receta ->
                scope.launch {
                    val resultado = if (receta.id.isBlank()) {
                        FirebaseRecetaRepository.agregarReceta(receta)
                    } else {
                        FirebaseRecetaRepository.actualizarReceta(receta).map { receta }
                    }
                    resultado
                        .onSuccess {
                            mensajeInfo = if (receta.id.isBlank()) "Receta creada." else "Receta actualizada."
                            mostrandoFormulario = false
                            recargar()
                        }
                        .onFailure { error ->
                            mensajeError = "No se pudo guardar: ${error.message}"
                        }
                }
            }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onVolver) {
                Text("← Volver")
            }
            Text(
                text = "Administrar recetas",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                recetaEnEdicion = null
                mostrandoFormulario = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Nueva receta")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (mensajeInfo.isNotEmpty()) {
            Text(
                text = mensajeInfo,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        when {
            cargando -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
                }
            }
            recetas.isEmpty() -> {
                Text("No hay recetas cargadas todavía.", style = MaterialTheme.typography.bodyMedium)
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(recetas, key = { it.id }) { receta ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "${receta.dia.uppercase()} - ${receta.categoria}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(receta.titulo, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ingredientes: ${receta.ingredientes.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = {
                                        recetaEnEdicion = receta
                                        mostrandoFormulario = true
                                    }) {
                                        Text("Editar")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                FirebaseRecetaRepository.eliminarReceta(receta.id)
                                                    .onSuccess {
                                                        mensajeInfo = "Receta eliminada."
                                                        recargar()
                                                    }
                                                    .onFailure { error ->
                                                        mensajeError = "No se pudo eliminar: ${error.message}"
                                                    }
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormularioReceta(
    recetaInicial: Receta?,
    onCancelar: () -> Unit,
    onGuardar: (Receta) -> Unit
) {
    var titulo by remember { mutableStateOf(recetaInicial?.titulo ?: "") }
    var dia by remember { mutableStateOf(recetaInicial?.dia ?: "") }
    var categoria by remember { mutableStateOf(recetaInicial?.categoria ?: "Almuerzo") }
    var ingredientesTexto by remember {
        mutableStateOf(recetaInicial?.ingredientes?.joinToString(", ") ?: "")
    }
    var recomendacion by remember { mutableStateOf(recetaInicial?.recomendacionNutricional ?: "") }
    var aptaParaTexto by remember {
        mutableStateOf(recetaInicial?.aptaPara?.joinToString(", ") ?: "Estándar / Familiar")
    }
    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (recetaInicial == null) "Nueva receta" else "Editar receta",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dia,
            onValueChange = { dia = it },
            label = { Text("Día (ej: Lunes)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría (ej: Almuerzo)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = ingredientesTexto,
            onValueChange = { ingredientesTexto = it },
            label = { Text("Ingredientes (separados por coma)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = recomendacion,
            onValueChange = { recomendacion = it },
            label = { Text("Recomendación nutricional") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = aptaParaTexto,
            onValueChange = { aptaParaTexto = it },
            label = { Text("Apta para (separado por coma: Estándar / Familiar, Vegetariana, Hipocalórica)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (titulo.isBlank() || dia.isBlank() || categoria.isBlank()) {
                    mensajeError = "Título, día y categoría son obligatorios."
                    return@Button
                }
                val receta = Receta(
                    id = recetaInicial?.id ?: "",
                    dia = dia.trim(),
                    titulo = titulo.trim(),
                    ingredientes = ingredientesTexto.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    recomendacionNutricional = recomendacion.trim(),
                    categoria = categoria.trim(),
                    aptaPara = aptaParaTexto.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        .ifEmpty { listOf("Estándar / Familiar") }
                )
                onGuardar(receta)
            }) {
                Text("Guardar")
            }

            OutlinedButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    }
}