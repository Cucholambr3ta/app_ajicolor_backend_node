package com.example.appajicolorgrupo4.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Función para crear un archivo temporal de imagen en el cache del dispositivo
 * @param context Contexto de la aplicación
 * @return File objeto del archivo temporal creado
 */
private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply {
        if(!exists()) mkdirs()
    }
    return File(storageDir, "IMG_${timeStamp}.jpg")
}

/**
 * Función para obtener la URI del archivo usando FileProvider
 * @param context Contexto de la aplicación
 * @param file Archivo del cual obtener la URI
 * @return Uri URI segura del archivo para compartir con otras apps
 */
private fun getImageUriForFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

/**
 * Componente de cámara reutilizable que permite capturar fotos usando la cámara nativa del dispositivo
 * 
 * Características:
 * - Captura de fotos con la cámara nativa
 * - Almacenamiento temporal en cache
 * - Preview de la imagen capturada
 * - Opción para eliminar la foto con confirmación
 * - Manejo de estados persistentes
 * - Feedback visual con Toast messages
 * 
 * @param modifier Modificador para personalizar el diseño del componente
 * @param title Título personalizado para el componente (opcional)
 * @param onPhotoTaken Callback que se ejecuta cuando se toma una foto exitosamente, recibe la URI como String
 * @param onPhotoDeleted Callback que se ejecuta cuando se elimina una foto
 */
@Composable
fun CameraApp(
    modifier: Modifier = Modifier,
    title: String = "Captura de foto con Cámara del dispositivo",
    onPhotoTaken: ((String) -> Unit)? = null,
    onPhotoDeleted: (() -> Unit)? = null
) {
    val context = LocalContext.current
    
    // Variable para almacenar la última foto tomada con la cámara
    var photoUriString by rememberSaveable { mutableStateOf<String?>(null) }
    
    // Variable que usa la cámara para tomar la foto
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    
    // Estado para mostrar o no el diálogo de confirmación
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Launcher para solicitar permisos de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permiso concedido, preparar para abrir cámara
            Toast.makeText(context, "Permiso concedido. Presiona el botón nuevamente.", Toast.LENGTH_SHORT).show()
        } else {
            // Permiso denegado
            Toast.makeText(context, "Se necesita permiso de cámara para tomar fotos", Toast.LENGTH_LONG).show()
        }
    }

    // Launcher para abrir la cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        // Si se tomó la foto correctamente
        if (success) {
            photoUriString = pendingCaptureUri?.toString()
            onPhotoTaken?.invoke(photoUriString!!)
            Toast.makeText(context, "Foto tomada correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
        pendingCaptureUri = null
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(12.dp))

            // Mostrar estado de la foto
            if (photoUriString.isNullOrEmpty()) {
                Text(
                    text = "No se han tomado fotos",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                // Preview de la imagen usando AsyncImage de Coil
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(Uri.parse(photoUriString))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto Tomada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))
            }

            // Botón para tomar foto
            Button(
                onClick = {
                    try {
                        // Solicitar permiso primero
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        // Crear archivo y lanzar cámara
                        val file = createTempImageFile(context)
                        val uri = getImageUriForFile(context, file)
                        pendingCaptureUri = uri
                        takePictureLauncher.launch(uri)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Error al abrir cámara: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                Text(
                    if (photoUriString.isNullOrEmpty()) "Tomar Foto"
                    else "Volver a Tomar Foto"
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Botón para eliminar foto (solo si hay una foto)
            if (!photoUriString.isNullOrEmpty()) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Text("Eliminar Foto")
                }
            }

            // Diálogo de confirmación para eliminar
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    containerColor = Color.White.copy(alpha = 0.25f), // 75% más transparente
                    title = {
                        Text(
                            "Confirmación",
                            color = Color.Black
                        )
                    },
                    text = {
                        Text(
                            "¿Desea eliminar la foto?",
                            color = Color.Black
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                photoUriString = null
                                showDeleteDialog = false
                                onPhotoDeleted?.invoke()
                                Toast.makeText(
                                    context,
                                    "Se ha eliminado la foto correctamente",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        ) {
                            Text("Aceptar", color = Color.Black)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text("Cancelar", color = Color.Black)
                        }
                    }
                )
            }
        }
    }
}

/**
 * Composable de preview para el componente CameraApp
 * Útil para visualizar el componente en el editor de Android Studio
 */
@Composable
fun CameraAppPreview() {
    MaterialTheme {
        CameraApp(
            title = "Demo de Cámara",
            onPhotoTaken = { uri -> 
                println("Foto tomada: $uri") 
            },
            onPhotoDeleted = { 
                println("Foto eliminada") 
            }
        )
    }
}