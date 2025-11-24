package com.example.appajicolorgrupo4.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Nuestro componente de diálogo genérico y reutilizable.
 *
 * @param onDismissRequest Se llama cuando el usuario pide cerrar
 * (tocando fuera o presionando "Cancelar").
 * @param onConfirm Se llama cuando el usuario presiona "Aceptar".
 * @param title El título del diálogo.
 * @param text El mensaje principal del diálogo.
 * @param confirmButtonText Texto para el botón de confirmar (default: "Aceptar").
 * @param dismissButtonText Texto para el botón de cancelar (default: "Cancelar").
 * Si es null, el botón no se muestra.
 */
@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String = "Aceptar",
    dismissButtonText: String? = "Cancelar" // Nulable para ocultarlo
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,

        title = {
            Text(text = title)
        },

        text = {
            Text(text = text)
        },

        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    // Importante: también cerramos el diálogo al confirmar
                    onDismissRequest()
                }
            ) {
                Text(confirmButtonText)
            }
        },

        dismissButton = {
            // Solo mostramos el botón si el texto no es nulo
            if (dismissButtonText != null) {
                TextButton(
                    onClick = onDismissRequest // Al cancelar, solo cerramos
                ) {
                    Text(dismissButtonText)
                }
            }
        }
    )
}

/**
 * Diálogo de confirmación con icono
 * Ideal para acciones críticas como eliminar o cerrar sesión
 */
@Composable
fun ConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    icon: ImageVector? = null,
    confirmButtonText: String = "Confirmar",
    dismissButtonText: String = "Cancelar",
    confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
    isDangerous: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = icon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDangerous)
                        MaterialTheme.colorScheme.error
                    else confirmButtonColor
                )
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text(dismissButtonText)
            }
        }
    )
}

/**
 * Diálogo informativo simple
 * Solo muestra información con un botón de OK
 */
@Composable
fun InfoDialog(
    onDismiss: () -> Unit,
    title: String,
    message: String,
    icon: ImageVector? = null,
    buttonText: String = "Entendido"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let {
            { Icon(imageVector = it, contentDescription = null) }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(buttonText)
            }
        }
    )
}

/**
 * Diálogo personalizado con contenido flexible
 * Permite insertar cualquier composable como contenido
 */
@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                content()
            }
        }
    }
}

/**
 * Diálogo de carga/loading
 * Muestra un indicador de progreso con un mensaje
 */
@Composable
fun LoadingDialog(
    message: String = "Cargando...",
    onDismissRequest: (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = { onDismissRequest?.invoke() },
        properties = DialogProperties(
            dismissOnBackPress = onDismissRequest != null,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.CircularProgressIndicator()
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * Diálogo con contenido personalizado y botones
 */
@Composable
fun DialogWithButtons(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    confirmButtonText: String = "Aceptar",
    dismissButtonText: String = "Cancelar",
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Contenido personalizado
                content()

                Spacer(modifier = Modifier.height(8.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest
                    ) {
                        Text(dismissButtonText)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onConfirm()
                            onDismissRequest()
                        }
                    ) {
                        Text(confirmButtonText)
                    }
                }
            }
        }
    }
}