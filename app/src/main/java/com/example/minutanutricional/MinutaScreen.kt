package com.example.minutanutricional

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

private val ANCHO_MINIMO_PARA_GRILLA = 600.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinutaScreen(
    onLogout: () -> Unit,
    usuarioActual: Usuario? = null,
    recetas: List<Receta> = RecetasRepository.recetasSemanales
) {
    val dias = listOf("Todos") + recetas.map { it.dia }.distinct()
    var diaSeleccionado by remember { mutableStateOf(dias.first()) }
    var expandidoDia by remember { mutableStateOf(false) }

    val tiposMinuta = listOf("Todas") + recetas.flatMap { it.aptaPara }.distinct()
    var tipoSeleccionado by remember {
        mutableStateOf(usuarioActual?.tipoMinuta?.takeIf { tiposMinuta.contains(it) } ?: "Todas")
    }
    var expandidoTipo by remember { mutableStateOf(false) }

    val recetasFiltradas = recetas
        .let { lista -> if (diaSeleccionado == "Todos") lista else lista.filter { it.dia.equals(diaSeleccionado, ignoreCase = true) } }
        .let { lista -> if (tipoSeleccionado == "Todas") lista else recetasRecomendadasPara(tipoSeleccionado, lista) }

    val resumenPorDia = contarRecetasPorDia(recetas)
    val resumenPorCategoria = agruparRecetasPorCategoria(recetas)

    val totalIng = totalIngredientes(recetas)
    val ingredientesDistintos = ingredientesUnicos(recetas)
    val recetaDestacada = recetaConMasIngredientes(recetas)
    val variedad = nivelDeVariedad(ingredientesDistintos.size)

    val uriHandler = LocalUriHandler.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val anchoDisponible = maxWidth

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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expandidoDia,
                    onExpandedChange = { expandidoDia = !expandidoDia },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = diaSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Día") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoDia) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandidoDia,
                        onDismissRequest = { expandidoDia = false }
                    ) {
                        dias.forEach { dia ->
                            DropdownMenuItem(
                                text = { Text(dia) },
                                onClick = {
                                    diaSeleccionado = dia
                                    expandidoDia = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandidoTipo,
                    onExpandedChange = { expandidoTipo = !expandidoTipo },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = tipoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de minuta") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoTipo) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandidoTipo,
                        onDismissRequest = { expandidoTipo = false }
                    ) {
                        tiposMinuta.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipoSeleccionado = tipo
                                    expandidoTipo = false
                                }
                            )
                        }
                    }
                }
            }

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
                        text = "Recetas por categoría: " +
                                resumenPorCategoria.entries.joinToString(", ") { (categoria, lista) ->
                                    "$categoria (${lista.size})"
                                },
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(4.dp))

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

            if (recetasFiltradas.isEmpty()) {
                Text(
                    text = "No hay recetas que coincidan con los filtros seleccionados.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else if (anchoDisponible >= ANCHO_MINIMO_PARA_GRILLA) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 220.dp),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    gridItems(recetasFiltradas) { receta ->
                        RecetaCard(receta)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recetasFiltradas) { receta ->
                        RecetaCard(receta)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
}

@Composable
private fun RecetaCard(receta: Receta) {
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
            if (receta.aptaPara.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Recomendada para: ${receta.aptaPara.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}