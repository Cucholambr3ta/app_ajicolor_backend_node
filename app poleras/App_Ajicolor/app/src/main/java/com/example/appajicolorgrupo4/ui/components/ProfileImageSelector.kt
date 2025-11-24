package com.example.appajicolorgrupo4.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Componente de selector de imagen de perfil
 * Permite seleccionar desde galería o tomar foto con la cámara
 */
@Composable
fun ProfileImageSelector(
    defaultImageRes: Int,
    onImageSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    currentImageUri: String? = null
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para solicitar permisos de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permiso concedido, abrir cámara
            try {
                val file = createImageFile(context)
                val uri = getUriForFile(context, file)
                pendingCaptureUri = uri
                // Necesitamos relanzar la cámara después de obtener el permiso
                Toast.makeText(context, "Permiso concedido. Abre de nuevo la cámara.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al crear archivo: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            // Permiso denegado
            Toast.makeText(context, "Se necesita permiso de cámara para tomar fotos", Toast.LENGTH_LONG).show()
        }
    }

    // Launcher para la galería usando Photo Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            onImageSelected(it.toString())
        }
    }

    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCaptureUri?.let {
                onImageSelected(it.toString())
                Toast.makeText(context, "Foto capturada exitosamente", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "No se capturó ninguna foto", Toast.LENGTH_SHORT).show()
        }
        pendingCaptureUri = null
    }

    // Imagen de perfil clickeable
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable { showDialog = true },
        contentAlignment = Alignment.Center
    ) {
        // Mostrar imagen según la URI
        if (currentImageUri != null) {
            // Intentar mostrar imagen desde URI
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(currentImageUri.toUri())
                    .crossfade(true)
                    .error(defaultImageRes) // Fallback a imagen por defecto
                    .build(),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Mostrar imagen por defecto
            Image(
                painter = painterResource(id = defaultImageRes),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        // Overlay con indicador de edición
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Cambiar foto",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        }
    }

    // Diálogo de selección
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color.White.copy(alpha = 0.25f), // 75% más transparente
            title = {
                Text(
                    "Foto de perfil",
                    color = Color.Black
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "¿Cómo deseas agregar tu foto?",
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón Cámara con solicitud de permisos
                    OutlinedButton(
                        onClick = {
                            showDialog = false
                            try {
                                // Solicitar permiso de cámara
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                // Preparar y lanzar la cámara
                                val file = createImageFile(context)
                                val uri = getUriForFile(context, file)
                                pendingCaptureUri = uri
                                cameraLauncher.launch(uri)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error al abrir cámara: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar Foto")
                    }

                    // Botón Galería con Photo Picker
                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar de Galería")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = Color.Black)
                }
            }
        )
    }
}

/**
 * Crea un archivo temporal para la imagen
 */
private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "profile_images").apply {
        if (!exists()) mkdirs()
    }
    return File(storageDir, "PROFILE_${timeStamp}.jpg")
}

/**
 * Obtiene la URI del archivo usando FileProvider
 */
private fun getUriForFile(context: Context, file: File): Uri {
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

