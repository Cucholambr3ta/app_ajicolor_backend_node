package com.example.appajicolorgrupo4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.data.ColorInfo

/**
 * Componente reutilizable para seleccionar un color de una lista.
 * Muestra los colores en una cuadrícula con círculos de colores.
 *
 * @param colores Lista de colores disponibles para seleccionar
 * @param colorSeleccionado El color actualmente seleccionado (puede ser null)
 * @param onColorSelected Callback que se ejecuta cuando se selecciona un color
 * @param modifier Modificador para personalizar el componente
 * @param columnas Número de columnas en la cuadrícula (por defecto 5)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSelector(
    colores: List<ColorInfo>,
    colorSeleccionado: ColorInfo?,
    onColorSelected: (ColorInfo) -> Unit,
    modifier: Modifier = Modifier,
    columnas: Int = 5
) {
    var expanded by remember { mutableStateOf(false) }

    // Seleccionar blanco por defecto si no hay color seleccionado
    LaunchedEffect(Unit) {
        if (colorSeleccionado == null) {
            val colorBlanco = colores.find { it.nombre.contains("Blanco", ignoreCase = true) }
            if (colorBlanco != null) {
                onColorSelected(colorBlanco)
            } else if (colores.isNotEmpty()) {
                onColorSelected(colores.first())
            }
        }
    }

    Column(modifier = modifier) {
        // Dropdown para seleccionar color
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = colorSeleccionado?.nombre ?: "Seleccionar color",
                onValueChange = {},
                readOnly = true,
                label = { Text("Color") },
                trailingIcon = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Círculo de vista previa del color
                        if (colorSeleccionado != null) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(colorSeleccionado.color)
                                    .border(1.dp, Color.Gray, CircleShape)
                            )
                        }
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                colores.forEach { colorInfo ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(colorInfo.color)
                                        .border(1.dp, Color.Gray, CircleShape)
                                )
                                Text(
                                    text = colorInfo.nombre,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (colorSeleccionado?.nombre == colorInfo.nombre) {
                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Seleccionado",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            onColorSelected(colorInfo)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Item individual de color que muestra un círculo con el color
 * y su nombre debajo.
 */
@Composable
private fun ColorItem(
    colorInfo: ColorInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            // Círculo del color
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colorInfo.color)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color.Black else Color.Gray,
                        shape = CircleShape
                    )
            )

            // Icono de check si está seleccionado
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = if (colorInfo.color.luminance() > 0.5f) Color.Black else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Nombre del color
        Text(
            text = colorInfo.nombre,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            modifier = Modifier.width(64.dp)
        )
    }
}

/**
 * Variante compacta del selector de colores que muestra solo círculos
 * sin nombres, útil para espacios reducidos.
 */
@Composable
fun ColorSelectorCompact(
    colores: List<ColorInfo>,
    colorSeleccionado: ColorInfo?,
    onColorSelected: (ColorInfo) -> Unit,
    modifier: Modifier = Modifier,
    columnas: Int = 8
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnas),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(colores) { colorInfo ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colorInfo.color)
                    .border(
                        width = if (colorSeleccionado?.nombre == colorInfo.nombre) 3.dp else 1.dp,
                        color = if (colorSeleccionado?.nombre == colorInfo.nombre) Color.Black else Color.Gray,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(colorInfo) },
                contentAlignment = Alignment.Center
            ) {
                if (colorSeleccionado?.nombre == colorInfo.nombre) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seleccionado",
                        tint = if (colorInfo.color.luminance() > 0.5f) Color.Black else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Selector de color con diálogo modal.
 * Muestra un botón que al presionarlo abre un diálogo con todos los colores disponibles.
 */
@Composable
fun ColorSelectorDialog(
    colores: List<ColorInfo>,
    colorSeleccionado: ColorInfo?,
    onColorSelected: (ColorInfo) -> Unit,
    modifier: Modifier = Modifier,
    titulo: String = "Seleccionar Color"
) {
    var showDialog by remember { mutableStateOf(false) }

    // Botón que muestra el color seleccionado
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = colorSeleccionado?.nombre ?: "Selecciona un color",
                style = MaterialTheme.typography.bodyLarge
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colorSeleccionado?.color ?: Color.Gray)
                    .border(1.dp, Color.Gray, CircleShape)
            )
        }
    }

    // Diálogo con la cuadrícula de colores
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(titulo) },
            text = {
                ColorSelector(
                    colores = colores,
                    colorSeleccionado = colorSeleccionado,
                    onColorSelected = { color ->
                        onColorSelected(color)
                        showDialog = false
                    },
                    columnas = 4
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

/**
 * Función de extensión para calcular la luminancia de un color
 * y determinar si es claro u oscuro.
 */
private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}

