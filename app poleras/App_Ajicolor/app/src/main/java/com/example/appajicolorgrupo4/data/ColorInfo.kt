package com.example.appajicolorgrupo4.data

import androidx.compose.ui.graphics.Color

/**
 * Clase de datos que representa la información de un color disponible
 * para las camisetas.
 *
 * @param nombre Nombre descriptivo del color (ej: "Rojo", "Azul Marino")
 * @param color El color en formato Compose Color
 * @param hexCode Código hexadecimal del color (ej: "#FF0000")
 */
data class ColorInfo(
    val nombre: String,
    val color: Color,
    val hexCode: String
)

