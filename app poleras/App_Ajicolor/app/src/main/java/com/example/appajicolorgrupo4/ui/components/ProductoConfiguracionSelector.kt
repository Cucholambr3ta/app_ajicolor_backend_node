package com.example.appajicolorgrupo4.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.data.*

/**
 * Componente inteligente que muestra los selectores apropiados según la categoría del producto.
 *
 * Lógica:
 * - Serigrafía y Corporativa: Solo adulto (tallas adulto, colores adulto)
 * - DTF: Permite elegir adulto o infantil (tallas y colores según selección)
 * - Accesorios: Solo colores adulto (sin tallas)
 *
 * @param categoria La categoría del producto
 * @param tipoSeleccionado El tipo seleccionado (adulto/infantil) - solo para DTF
 * @param onTipoChanged Callback cuando cambia el tipo - solo para DTF
 * @param tallaSeleccionada La talla seleccionada actualmente
 * @param onTallaSelected Callback cuando se selecciona una talla
 * @param colorSeleccionado El color seleccionado actualmente
 * @param onColorSelected Callback cuando se selecciona un color
 * @param modifier Modificador para personalizar el componente
 */
@Composable
fun ProductoConfiguracionSelector(
    categoria: CategoriaProducto,
    tipoSeleccionado: TipoProducto,
    onTipoChanged: (TipoProducto) -> Unit,
    tallaSeleccionada: Talla?,
    onTallaSelected: (Talla) -> Unit,
    colorSeleccionado: ColorInfo?,
    onColorSelected: (ColorInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        // ← ELIMINADO .verticalScroll() porque ya está dentro de LazyColumn
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Selector de Tipo (solo para DTF)
        if (categoria.permiteSeleccionTipo()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Tipo de Producto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    TipoProductoSelector(
                        tipoSeleccionado = tipoSeleccionado,
                        onTipoSelected = onTipoChanged
                    )
                }
            }
        }

        // Selector de Talla (no para accesorios)
        if (categoria.usaTallas()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Selecciona la Talla",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = obtenerDescripcionTallas(categoria, tipoSeleccionado),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    val tallas = Talla.porCategoria(categoria, tipoSeleccionado)
                    TallaSelector(
                        tallas = tallas,
                        tallaSeleccionada = tallaSeleccionada,
                        onTallaSelected = onTallaSelected,
                        columnas = if (tipoSeleccionado == TipoProducto.ADULTO) 3 else 4
                    )
                }
            }
        }

        // Selector de Color
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Selecciona el Color",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = obtenerDescripcionColores(categoria, tipoSeleccionado),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                val colores = obtenerColoresSegunCategoria(categoria, tipoSeleccionado)
                ColorSelector(
                    colores = colores,
                    colorSeleccionado = colorSeleccionado,
                    onColorSelected = onColorSelected,
                    columnas = 5
                )
            }
        }
    }
}

/**
 * Selector de tipo de producto (Adulto/Infantil)
 */
@Composable
private fun TipoProductoSelector(
    tipoSeleccionado: TipoProducto,
    onTipoSelected: (TipoProducto) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TipoProducto.entries.forEach { tipo ->
            OutlinedButton(
                onClick = { onTipoSelected(tipo) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (tipoSeleccionado == tipo)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surface,
                    contentColor = if (tipoSeleccionado == tipo)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (tipoSeleccionado == tipo) 2.dp else 1.dp,
                    color = if (tipoSeleccionado == tipo)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = tipo.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (tipoSeleccionado == tipo) FontWeight.Bold else FontWeight.Normal
                    )
                    if (tipoSeleccionado == tipo) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * Obtiene los colores disponibles según la categoría y tipo
 */
private fun obtenerColoresSegunCategoria(
    categoria: CategoriaProducto,
    tipo: TipoProducto
): List<ColorInfo> {
    return when (categoria) {
        CategoriaProducto.SERIGRAFIA,
        CategoriaProducto.CORPORATIVA,
        CategoriaProducto.ACCESORIOS -> ColoresDisponibles.coloresAdulto

        CategoriaProducto.DTF -> when (tipo) {
            TipoProducto.ADULTO -> ColoresDisponibles.coloresAdulto
            TipoProducto.INFANTIL -> ColoresDisponibles.coloresInfantil
        }
    }
}

/**
 * Obtiene la descripción de las tallas según la categoría
 */
private fun obtenerDescripcionTallas(
    categoria: CategoriaProducto,
    tipo: TipoProducto
): String {
    return when (categoria) {
        CategoriaProducto.SERIGRAFIA -> "Tallas adulto disponibles: S, M, L, XL, 2XL, 3XL"
        CategoriaProducto.CORPORATIVA -> "Tallas corporativas: S, M, L, XL, 2XL, 3XL"
        CategoriaProducto.DTF -> when (tipo) {
            TipoProducto.ADULTO -> "Tallas adulto: S, M, L, XL, 2XL, 3XL"
            TipoProducto.INFANTIL -> "Tallas infantiles: 2, 4, 6, 8, 10, 12, 14, 16"
        }
        CategoriaProducto.ACCESORIOS -> ""
    }
}

/**
 * Obtiene la descripción de los colores según la categoría
 */
private fun obtenerDescripcionColores(
    categoria: CategoriaProducto,
    tipo: TipoProducto
): String {
    return when (categoria) {
        CategoriaProducto.SERIGRAFIA -> "34 colores disponibles para serigrafía"
        CategoriaProducto.CORPORATIVA -> "Paleta corporativa de 34 colores"
        CategoriaProducto.DTF -> when (tipo) {
            TipoProducto.ADULTO -> "34 colores disponibles para adulto"
            TipoProducto.INFANTIL -> "15 colores vibrantes para niños"
        }
        CategoriaProducto.ACCESORIOS -> "Colores disponibles para accesorios"
    }
}

