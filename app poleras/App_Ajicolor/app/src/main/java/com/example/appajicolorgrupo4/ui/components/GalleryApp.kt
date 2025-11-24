package com.example.appajicolorgrupo4.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Componente de galería para seleccionar imágenes desde el dispositivo
 * Usa el Photo Picker moderno de Android
 *
 * @param onImageSelected Callback que se ejecuta cuando se selecciona una imagen
 * @param modifier Modificador para personalizar el diseño
 * @param buttonText Texto del botón (por defecto "Seleccionar imagen")
 */
@Composable
fun GalleryImagePicker(
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = "Seleccionar imagen"
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para el Photo Picker
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            onImageSelected(uri)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar imagen seleccionada si existe
        selectedImageUri?.let { uri ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                // Aquí se mostraría la imagen desde la URI
                // Por ahora mostramos un placeholder
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Imagen seleccionada")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Botón para seleccionar imagen
        Button(
            onClick = {
                pickImageLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(buttonText)
        }
    }
}

/**
 * Componente simple de galería - Solo botón
 * Ideal para integrar en diálogos o formularios
 *
 * @param onImageSelected Callback con la URI de la imagen seleccionada
 * @param buttonText Texto del botón
 * @param modifier Modificador
 */
@Composable
fun GalleryPickerButton(
    onImageSelected: (Uri) -> Unit,
    buttonText: String = "Seleccionar de Galería",
    modifier: Modifier = Modifier
) {
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onImageSelected(it) }
    }

    Button(
        onClick = {
            pickImageLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        },
        modifier = modifier
    ) {
        Text(buttonText)
    }
}

/**
 * Componente de galería con múltiples selecciones
 * Permite seleccionar varias imágenes a la vez
 *
 * @param onImagesSelected Callback con la lista de URIs seleccionadas
 * @param maxImages Número máximo de imágenes a seleccionar
 * @param modifier Modificador
 */
@Composable
fun MultipleGalleryPicker(
    onImagesSelected: (List<Uri>) -> Unit,
    maxImages: Int = 5,
    modifier: Modifier = Modifier
) {
    val pickMultipleImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxImages)
    ) { uris ->
        if (uris.isNotEmpty()) {
            onImagesSelected(uris)
        }
    }

    Button(
        onClick = {
            pickMultipleImagesLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        },
        modifier = modifier
    ) {
        Text("Seleccionar hasta $maxImages imágenes")
    }
}
