package com.example.minutanutricional



data class Receta(
    val id: Int,
    val dia: String,
    val titulo: String,
    val ingredientes: List<String>,
    val recomendacionNutricional: String,
    val categoria: String

)

object  RecetasRepository{
    val recetasSemanales = listOf(
        Receta(
            id=1,
            dia = "lunes",
            titulo = "Cazuela de Pollo Tradicional",
            ingredientes = listOf("Pollo", "Papa", "Zapallo", "Choclo", "Porotos verdes"),
            recomendacionNutricional = "Alta en proteínas de fácil digestión y aporte calórico balanceado.",
            categoria = "Almuerzo"
        ),
        Receta(
            id = 2,
            dia = "Martes",
            titulo = "Charquicán de Verduras y Carne",
            ingredientes = listOf("Carne molida", "Papas", "Zapallo", "Choclo", "Huevo frito opcional"),
            recomendacionNutricional = "Rico en fibra vegetal, hierro y vitaminas del complejo B.",
            categoria = "Almuerzo"
        ),
        Receta(
            id = 3,
            dia = "Miércoles",
            titulo = "Pescado al Horno con Verduras Asadas",
            ingredientes = listOf("Reineta o Merluza", "Zanahoria", "Pimentón", "Cebolla", "Aceite de oliva"),
            recomendacionNutricional = "Excelente fuente de Omega-3 para la salud cardiovascular.",
            categoria = "Almuerzo"
        ),
        Receta(
            id = 4,
            dia = "Jueves",
            titulo = "Lentejas Guisadas con Verduras",
            ingredientes = listOf("Lentejas", "Zanahoria", "Espinaca", "Zapallo", "Arroz"),
            recomendacionNutricional = "Alto contenido de hierro y proteína vegetal.",
            categoria = "Almuerzo"
        ),
        Receta(
            id = 5,
            dia = "Viernes",
            titulo = "Tallarines con Salsa de Tomate Natural y Atún",
            ingredientes = listOf("Pasta integral", "Tomate natural", "Atún en agua", "Orégano"),
            recomendacionNutricional = "Aporte de carbohidratos complejos para energía sostenida.",
            categoria = "Almuerzo"
        )
    )
}