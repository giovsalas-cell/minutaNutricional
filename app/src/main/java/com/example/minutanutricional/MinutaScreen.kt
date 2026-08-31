package com.example.minutanutricional

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinutaScreen(
    onLogout: () -> Unit,
    usuarioActual: Usuario? = null,
    recetas: List<Receta> = RecetasRepository.recetasSemanales
) {
    // Lista de días para el combo box, incluyendo la opción "Todos"
    val dias = listOf("Todos") + recetas.map { it.dia }.distinct()
    var diaSeleccionado by remember { mutableStateOf(dias.first()) }
    var expandido by remember { mutableStateOf(false) }

    // Filtra la lista según el día elegido en el combo box
    val recetasFiltradas = if (diaSeleccionado == "Todos") {
        recetas
    } else {
        recetas.filter { it.dia.equals(diaSeleccionado, ignoreCase = true) }
    }

    // --- Datos del resumen semanal, calculados con funciones + colecciones ---


    val resumenPorDia = contarRecetasPorDia(recetas)
    val totalIng = totalIngredientes(recetas)
    val ingredientesDistintos = ingredientesUnicos(recetas)
    val recetaDestacada = recetaConMasIngredientes(recetas)
    val variedad = nivelDeVariedad(ingredientesDistintos.size)

    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (usuarioActual != null) {
                "¡Bienvenido, ${usuarioActual.nombre}!"
            } else {
                "¡Bienvenido a la Minuta Nutricional!"
            },
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (usuarioActual != null) {
            Text(
                text = "Tipo de minuta: ${usuarioActual.tipoMinuta}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Combo box (dropdown) para filtrar por día ---
        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = !expandido },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = diaSeleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filtrar por día") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                dias.forEach { dia ->
                    DropdownMenuItem(
                        text = { Text(dia) },
                        onClick = {
                            diaSeleccionado = dia
                            expandido = false
                        }
                    )
                }
            }
        }

        // --- Tarjeta con la tabla/grilla de resumen semanal ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Resumen semanal",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // --- Encabezado de la tabla ---
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Día",
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .padding(6.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "N° Recetas",
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .padding(6.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // --- Filas de la tabla generadas con un bucle for sobre el mapa ---
                for ((dia, cantidad) in resumenPorDia) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = dia,
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, MaterialTheme.colorScheme.outline)
                                .padding(6.dp)
                        )
                        Text(
                            text = cantidad.toString(),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, MaterialTheme.colorScheme.outline)
                                .padding(6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Total de ingredientes usados en la semana: $totalIng",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Ingredientes distintos: ${ingredientesDistintos.size} ($variedad)",
                    style = MaterialTheme.typography.bodySmall
                )
                if (recetaDestacada != null) {
                    Text(
                        text = "Receta con más ingredientes: ${recetaDestacada.titulo} (${recetaDestacada.ingredientes.size})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // LazyColumn renderiza la lista filtrada
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recetasFiltradas) { receta ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${receta.dia.uppercase()} - ${receta.categoria}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = receta.titulo,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ingredientes: ${receta.ingredientes.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nota: ${receta.recomendacionNutricional}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Vínculo externo (link) a una guía de alimentación saludable ---
        TextButton(onClick = {
            uriHandler.openUri("https://www.minsal.cl/guias-alimentarias/")
        }) {
            Text("Ver guías de alimentación saludable (MINSAL)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onLogout) {
            Text("Cerrar Sesión")
        }
    }
}