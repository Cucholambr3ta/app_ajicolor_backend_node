package com.example.appajicolorgrupo4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji

/**
 * Componente reutilizable que muestra el fondo de la aplicación
 * Usa un degradado de colores en lugar de imagen para mejor rendimiento
 */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MoradoAji,
                        Color(0xFF6B4E9C), // Morado intermedio
                        Color(0xFF9B7BBE)  // Morado claro
                    )
                )
            )
    ) {
        // Contenido de la pantalla encima del fondo
        content()
    }
}

