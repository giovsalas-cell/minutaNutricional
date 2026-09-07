package com.example.minutanutricional

/**
 * Funciones utilitarias que aplican sintaxis y colecciones de Kotlin
 * sobre la lista de recetas, para dar una mejor solución a los
 * requerimientos del cliente (criterio 4 de la pauta: integración de
 * funciones y colecciones de Kotlin).
 */

/**
 * Agrupa las recetas por categoría (Almuerzo, Postre, etc.)
 * usando la función de colección groupBy.
 */
fun agruparRecetasPorCategoria(recetas: List<Receta>): Map<String, List<Receta>> {
    return recetas.groupBy { it.categoria }
}

/**
 * Cuenta cuántas recetas hay por día de la semana.
 * Se implementa con un bucle for recorriendo la colección,
 * y un mapa mutable para acumular el conteo.
 */
fun contarRecetasPorDia(recetas: List<Receta>): Map<String, Int> {
    val conteo = mutableMapOf<String, Int>()
    for (receta in recetas) {
        val diaKey = receta.dia.replaceFirstChar { it.uppercase() }
        conteo[diaKey] = (conteo[diaKey] ?: 0) + 1
    }
    return conteo
}

/**
 * Suma la cantidad total de ingredientes usados en la semana completa,
 * usando la función de colección sumOf.
 */
fun totalIngredientes(recetas: List<Receta>): Int {
    return recetas.sumOf { it.ingredientes.size }
}

/**
 * Obtiene la lista de ingredientes únicos usados en toda la semana,
 * combinando flatMap (aplana las listas de ingredientes de cada receta),
 * distinct (elimina duplicados) y sorted (ordena alfabéticamente).
 */
fun ingredientesUnicos(recetas: List<Receta>): List<String> {
    return recetas.flatMap { it.ingredientes }.distinct().sorted()
}

/**
 * Encuentra la receta con más ingredientes usando maxByOrNull.
 * Retorna null si la lista está vacía (seguridad nula de Kotlin).
 */
fun recetaConMasIngredientes(recetas: List<Receta>): Receta? {
    return recetas.maxByOrNull { it.ingredientes.size }
}

/**
 * Clasifica el nivel de variedad de ingredientes de la semana
 * usando un condicional when con rangos.
 */
fun nivelDeVariedad(cantidadIngredientesUnicos: Int): String {
    return when {
        cantidadIngredientesUnicos >= 15 -> "Alta variedad"
        cantidadIngredientesUnicos in 8..14 -> "Variedad media"
        else -> "Variedad baja"
    }
}

/**
 * NUEVO: filtra las recetas que son apropiadas para un tipo de minuta
 * específico (Estándar / Familiar, Vegetariana, Hipocalórica), usando
 * el campo Receta.aptaPara y la función de colección filter/any.
 *
 * Esto aprovecha el dato tipoMinuta que ya se solicita en el Registro,
 * personalizando la sugerencia semanal de acuerdo al perfil del usuario.
 */
fun recetasRecomendadasPara(tipoMinuta: String, recetas: List<Receta>): List<Receta> {
    return recetas.filter { receta -> receta.aptaPara.any { it.equals(tipoMinuta, ignoreCase = true) } }
}

/**
 * NUEVO: cuenta cuántas recetas de la semana son compatibles con cada
 * tipo de minuta existente, útil para mostrar en el resumen semanal
 * qué tan variada es la oferta para cada perfil.
 */
fun contarRecetasPorTipoMinuta(recetas: List<Receta>): Map<String, Int> {
    return recetas
        .flatMap { it.aptaPara }
        .groupingBy { it }
        .eachCount()
}