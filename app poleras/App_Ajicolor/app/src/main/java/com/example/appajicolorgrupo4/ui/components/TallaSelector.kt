package com.example.appajicolorgrupo4.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appajicolorgrupo4.data.Talla

/**
 * Componente para seleccionar una talla de una lista disponible.
 * Muestra las tallas en un dropdown similar al selector de colores.
 *
 * @param tallas Lista de tallas disponibles
 * @param tallaSeleccionada La talla actualmente seleccionada
 * @param onTallaSelected Callback cuando se selecciona una talla
 * @param modifier Modificador para personalizar el componente
 * @param columnas Número de columnas en la cuadrícula (deprecated, se mantiene por compatibilidad)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TallaSelector(
    tallas: List<Talla>,
    tallaSeleccionada: Talla?,
    onTallaSelected: (Talla) -> Unit,
    modifier: Modifier = Modifier,
    columnas: Int = 4
) {
    var expanded by remember { mutableStateOf(false) }

    // Seleccionar talla S por defecto si no hay talla seleccionada
    LaunchedEffect(Unit) {
        if (tallaSeleccionada == null && tallas.isNotEmpty()) {
            val tallaS = tallas.find { it.displayName == "S" }
            if (tallaS != null) {
                onTallaSelected(tallaS)
            } else {
                onTallaSelected(tallas.first())
            }
        }
    }

    Column(modifier = modifier) {
        // Dropdown para seleccionar talla
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = tallaSeleccionada?.displayName ?: "Seleccionar talla",
                onValueChange = {},
                readOnly = true,
                label = { Text("Talla") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                tallas.forEach { talla ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = talla.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (tallaSeleccionada == talla) FontWeight.Bold else FontWeight.Normal
                                )
                                if (tallaSeleccionada == talla) {
                                    Text(
                                        text = "✓",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        onClick = {
                            onTallaSelected(talla)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Chip individual para mostrar una talla
 */
@Composable
private fun TallaChip(
    talla: Talla,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                Color.Transparent,
            contentColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = talla.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Variante compacta del selector de tallas
 * Muestra las tallas en una fila horizontal con scroll
 */
@Composable
fun TallaSelectorCompact(
    tallas: List<Talla>,
    tallaSeleccionada: Talla?,
    onTallaSelected: (Talla) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tallas.forEach { talla ->
            FilterChip(
                selected = tallaSeleccionada == talla,
                onClick = { onTallaSelected(talla) },
                label = {
                    Text(
                        text = talla.displayName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

/**
 * Selector de talla con diálogo modal
 */
@Composable
fun TallaSelectorDialog(
    tallas: List<Talla>,
    tallaSeleccionada: Talla?,
    onTallaSelected: (Talla) -> Unit,
    modifier: Modifier = Modifier,
    titulo: String = "Seleccionar Talla"
) {
    var showDialog by remember { mutableStateOf(false) }

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
                text = tallaSeleccionada?.displayName ?: "Selecciona una talla",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "▼",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(titulo) },
            text = {
                TallaSelector(
                    tallas = tallas,
                    tallaSeleccionada = tallaSeleccionada,
                    onTallaSelected = { talla ->
                        onTallaSelected(talla)
                        showDialog = false
                    },
                    columnas = 3
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

