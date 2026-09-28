package com.example.minutanutricional

/**
 * IMPORTANTE: para que Firebase Realtime Database pueda convertir
 * automáticamente cada nodo JSON en un objeto Receta (y viceversa), la
 * clase necesita un constructor sin argumentos, es decir, todos los
 * parámetros deben tener un valor por defecto. Por eso 'id' cambia de Int a
 * String (Firebase genera llaves de tipo String con push()) y todos los
 * campos tienen valores por defecto.
 */
data class Receta(
    val id: String = "",
    val dia: String = "",
    val titulo: String = "",
    val ingredientes: List<String> = emptyList(),
    val recomendacionNutricional: String = "",
    val categoria: String = "",
    // Tipos de minuta (ver Usuario.tipoMinuta) para los que esta receta es
    // especialmente recomendada. Permite personalizar la minuta semanal
    // de acuerdo a la preferencia declarada por el usuario en el Registro.
    val aptaPara: List<String> = listOf("Estándar / Familiar")
)

/**
 * Datos de ejemplo usados SOLO como "semilla": la primera vez que la app se
 * conecta a una Realtime Database vacía, FirebaseRecetaRepository sube estas
 * recetas para que la base no quede sin contenido. Una vez migradas, el
 * origen de verdad pasa a ser Firebase, no esta lista en memoria.
 */
object RecetasRepository {
    val recetasSemanales = listOf(
        Receta(
            id = "seed-1",
            dia = "Lunes",
            titulo = "Cazuela de Pollo Tradicional",
            ingredientes = listOf("Pollo", "Papa", "Zapallo", "Choclo", "Porotos verdes"),
            recomendacionNutricional = "Alta en proteínas de fácil digestión y aporte calórico balanceado.",
            categoria = "Almuerzo",
            aptaPara = listOf("Estándar / Familiar")
        ),
        Receta(
            id = "seed-2",
            dia = "Martes",
            titulo = "Charquicán de Verduras y Carne",
            ingredientes = listOf("Carne molida", "Papas", "Zapallo", "Choclo", "Huevo frito opcional"),
            recomendacionNutricional = "Rico en fibra vegetal, hierro y vitaminas del complejo B.",
            categoria = "Almuerzo",
            aptaPara = listOf("Estándar / Familiar")
        ),
        Receta(
            id = "seed-3",
            dia = "Miércoles",
            titulo = "Pescado al Horno con Verduras Asadas",
            ingredientes = listOf("Reineta o Merluza", "Zanahoria", "Pimentón", "Cebolla", "Aceite de oliva"),
            recomendacionNutricional = "Excelente fuente de Omega-3 para la salud cardiovascular.",
            categoria = "Almuerzo",
            aptaPara = listOf("Estándar / Familiar", "Hipocalórica")
        ),
        Receta(
            id = "seed-4",
            dia = "Jueves",
            titulo = "Lentejas Guisadas con Verduras",
            ingredientes = listOf("Lentejas", "Zanahoria", "Espinaca", "Zapallo", "Arroz"),
            recomendacionNutricional = "Alto contenido de hierro y proteína vegetal.",
            categoria = "Almuerzo",
            aptaPara = listOf("Estándar / Familiar", "Vegetariana", "Hipocalórica")
        ),
        Receta(
            id = "seed-5",
            dia = "Viernes",
            titulo = "Tallarines con Salsa de Tomate Natural y Atún",
            ingredientes = listOf("Pasta integral", "Tomate natural", "Atún en agua", "Orégano"),
            recomendacionNutricional = "Aporte de carbohidratos complejos para energía sostenida.",
            categoria = "Almuerzo",
            aptaPara = listOf("Estándar / Familiar")
        )
    )
}
