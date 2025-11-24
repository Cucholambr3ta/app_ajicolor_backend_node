package com.example.appajicolorgrupo4.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.data.*
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.*
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.CarritoViewModel
import com.example.appajicolorgrupo4.viewmodel.ProductoViewModel
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: String,
    navController: NavController,
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel
) {
    val producto = remember(productoId) {
        CatalogoProductos.obtenerTodos().find { it.id == productoId }
    }

    // Si el producto no existe, mostrar error
    if (producto == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo
            Image(
                painter = painterResource(id = com.example.appajicolorgrupo4.R.drawable.fondo_app),
                contentDescription = "Fondo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Contenido
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Error") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                },
                containerColor = Color.Transparent
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "❌",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Producto no encontrado",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "El producto con ID: $productoId no existe en el catálogo",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }
        }
        return
    }

    // Estados para la configuración del producto
    var tipoSeleccionado by remember {
        mutableStateOf(producto.tipoProducto ?: TipoProducto.ADULTO)
    }
    var tallaSeleccionada by remember { mutableStateOf<Talla?>(null) }
    var colorSeleccionado by remember { mutableStateOf<ColorInfo?>(null) }
    var cantidad by remember { mutableStateOf(1) }

    // Inicializar talla y color por defecto
    LaunchedEffect(producto) {
        // Inicializar talla con S si el producto requiere talla
        if (producto.requiereTalla() && tallaSeleccionada == null) {
            val tallas = Talla.porCategoria(producto.categoria, tipoSeleccionado)
            val tallaS = tallas.find { it.displayName == "S" }
            tallaSeleccionada = tallaS ?: tallas.firstOrNull()
        }

        // Inicializar color con blanco si el producto requiere color
        if (producto.requiereColor() && colorSeleccionado == null) {
            val colores = when (producto.categoria) {
                CategoriaProducto.SERIGRAFIA,
                CategoriaProducto.CORPORATIVA,
                CategoriaProducto.ACCESORIOS -> ColoresDisponibles.coloresAdulto
                CategoriaProducto.DTF -> when (tipoSeleccionado) {
                    TipoProducto.ADULTO -> ColoresDisponibles.coloresAdulto
                    TipoProducto.INFANTIL -> ColoresDisponibles.coloresInfantil
                }
            }
            val colorBlanco = colores.find { it.nombre.contains("Blanco", ignoreCase = true) }
            colorSeleccionado = colorBlanco ?: colores.firstOrNull()
        }
    }

    // Estado para reseñas
    var mostrarDialogoResena by remember { mutableStateOf(false) }
    val resenas by productoViewModel.resenas.collectAsState()
    val resenasProducto = resenas[productoId] ?: emptyList()

    // Mensaje de confirmación
    var mostrarMensajeAgregado by remember { mutableStateOf(false) }

    // Estados para drawer y navigation
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

    // Reset al cambiar tipo (solo para DTF)
    LaunchedEffect(tipoSeleccionado) {
        if (producto.permiteSeleccionTipo()) {
            tallaSeleccionada = null
            colorSeleccionado = null
            // Reinicializar con valores por defecto
            val tallas = Talla.porCategoria(producto.categoria, tipoSeleccionado)
            val tallaS = tallas.find { it.displayName == "S" } ?: tallas.find { it.displayName == "2" }
            tallaSeleccionada = tallaS ?: tallas.firstOrNull()

            val colores = when (tipoSeleccionado) {
                TipoProducto.ADULTO -> ColoresDisponibles.coloresAdulto
                TipoProducto.INFANTIL -> ColoresDisponibles.coloresInfantil
            }
            val colorBlanco = colores.find { it.nombre.contains("Blanco", ignoreCase = true) }
            colorSeleccionado = colorBlanco ?: colores.firstOrNull()
        }
    }

    // Estructura sin anidamientos problemáticos
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(id = com.example.appajicolorgrupo4.R.drawable.fondo_app),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido principal
        AppNavigationDrawer(
            navController = navController,
            drawerState = drawerState,
            currentRoute = currentRoute
        ) {
            Scaffold(
                topBar = {
                    TopBarWithCart(
                        title = producto.nombre,
                        navController = navController,
                        drawerState = drawerState,
                        scope = scope,
                        additionalActions = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                },
                containerColor = Color.Transparent,
                snackbarHost = {
                    if (mostrarMensajeAgregado) {
                        Snackbar(
                            modifier = Modifier.padding(16.dp),
                            action = {
                                TextButton(onClick = {
                                    mostrarMensajeAgregado = false
                                    navController.navigate(Screen.Cart.route)
                                }) {
                                    Text("Ver Carrito")
                                }
                            }
                        ) {
                            Text("✓ Producto agregado al carrito")
                        }
                    }
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                // Imagen del producto
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                        )
                    ) {
                        Image(
                            painter = painterResource(id = producto.imagenResId),
                            contentDescription = producto.nombre,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Información básica
                item {
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
                                text = producto.nombre,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MoradoAji
                            )

                            // Calificación
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CalificacionEstrellas(producto.calificacionPromedio)
                                Text(
                                    text = "${producto.calificacionPromedio} (${producto.numeroResenas} reseñas)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MoradoAji
                                )
                            }

                            HorizontalDivider()

                            // Precio
                            Text(
                                text = producto.precioFormateado(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MoradoAji
                            )

                            // Descripción con formato
                            val descripcionFormateada = remember {
                                buildAnnotatedString {
                                    producto.descripcion.split("\n").forEach { linea ->
                                        if (linea.contains("**")) {
                                            val partes = linea.split("**")
                                            partes.forEachIndexed { index, parte ->
                                                if (index % 2 == 1) {
                                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                        append(parte)
                                                    }
                                                } else {
                                                    append(parte)
                                                }
                                            }
                                        } else {
                                            append(linea)
                                        }
                                        append("\n")
                                    }
                                }
                            }

                            Text(
                                text = descripcionFormateada,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MoradoAji
                            )

                            // Stock
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (producto.stock > 0)
                                        MoradoAji
                                    else
                                        MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (producto.stock > 0)
                                        "En stock (${producto.stock} disponibles)"
                                    else
                                        "Agotado",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (producto.stock > 0)
                                        MoradoAji
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                // Configuración del producto según categoría
                if (producto.permiteSeleccionTipo() || producto.requiereTalla() || producto.requiereColor()) {
                    item {
                        ProductoConfiguracionSelector(
                            categoria = producto.categoria,
                            tipoSeleccionado = tipoSeleccionado,
                            onTipoChanged = { tipoSeleccionado = it },
                            tallaSeleccionada = tallaSeleccionada,
                            onTallaSelected = { tallaSeleccionada = it },
                            colorSeleccionado = colorSeleccionado,
                            onColorSelected = { colorSeleccionado = it }
                        )
                    }
                }

                // Selector de cantidad
                item {
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
                                text = "Cantidad",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MoradoAji
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                IconButton(
                                    onClick = { if (cantidad > 1) cantidad-- },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Text("−", style = MaterialTheme.typography.headlineMedium, color = MoradoAji)
                                }

                                Text(
                                    text = cantidad.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MoradoAji,
                                    modifier = Modifier.widthIn(min = 40.dp)
                                )

                                IconButton(
                                    onClick = { if (cantidad < producto.stock) cantidad++ },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Default.Add, "Aumentar", tint = MoradoAji)
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                Text(
                                    text = "Total: $${producto.precio * cantidad}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MoradoAji
                                )
                            }
                        }
                    }
                }

                // Botón agregar al carrito
                item {
                    val configuracion = ProductoConfiguracion(
                        producto = producto,
                        talla = tallaSeleccionada,
                        color = colorSeleccionado,
                        cantidad = cantidad
                    )

                    val esValida = configuracion.esValida()

                    // Debug logging
                    android.util.Log.d("DetalleProducto", """
                        Configuración:
                        - Producto: ${producto.nombre}
                        - Talla: ${tallaSeleccionada?.displayName ?: "null"}
                        - Color: ${colorSeleccionado?.nombre ?: "null"}
                        - Cantidad: $cantidad
                        - Es válida: $esValida
                        - Requiere talla: ${producto.requiereTalla()}
                        - Requiere color: ${producto.requiereColor()}
                    """.trimIndent())

                    Button(
                        onClick = {
                            android.util.Log.d("DetalleProducto", "Botón agregar presionado. esValida=$esValida")
                            if (esValida) {
                                try {
                                    val productoCarrito = configuracion.toProductoCarrito()
                                    android.util.Log.d("DetalleProducto", "ProductoCarrito creado: $productoCarrito")

                                    carritoViewModel.agregarProducto(productoCarrito)
                                    android.util.Log.d("DetalleProducto", "Producto agregado al ViewModel")

                                    mostrarMensajeAgregado = true
                                    android.util.Log.d("DetalleProducto", "Mensaje de confirmación activado")
                                } catch (e: Exception) {
                                    android.util.Log.e("DetalleProducto", "Error al agregar al carrito", e)
android.util.Log.e("DetalleProducto", "Error al agregar al carrito", e)

                                }
                            } else {
                                android.util.Log.w("DetalleProducto", "Configuración no válida, no se agregó al carrito")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = esValida && producto.stock > 0
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (!esValida) {
                                "Completa la configuración"
                            } else {
                                "Agregar al Carrito"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (!esValida) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Por favor completa:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Bold
                                )
                                if (producto.requiereTalla() && tallaSeleccionada == null) {
                                    Text(
                                        text = "• Selecciona una talla",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                if (producto.requiereColor() && colorSeleccionado == null) {
                                    Text(
                                        text = "• Selecciona un color",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // Sección de reseñas
                item {
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Reseñas (${resenasProducto.size})",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MoradoAji
                                )

                                Button(
                                    onClick = { mostrarDialogoResena = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AmarilloAji,
                                        contentColor = MoradoAji
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, MoradoAji)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = MoradoAji)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Escribir Reseña", color = MoradoAji)
                                }
                            }

                            if (resenasProducto.isEmpty()) {
                                Text(
                                    text = "Sé el primero en dejar una reseña",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MoradoAji.copy(alpha = 0.6f)
                                )
                            } else {
                                resenasProducto.forEach { resena ->
                                    ResenaItem(resena)
                                    if (resena != resenasProducto.last()) {
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }

                // Espacio final
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            } // Fin LazyColumn
        } // Fin Scaffold
        } // Fin AppNavigationDrawer
    } // Fin Box

    // Diálogo para agregar reseña
    if (mostrarDialogoResena) {
        DialogoAgregarResena(
            productoId = productoId,
            onDismiss = { mostrarDialogoResena = false },
            onResenaAgregada = { resena ->
                productoViewModel.agregarResena(resena)
                mostrarDialogoResena = false
            }
        )
    }

    // Auto-ocultar mensaje después de 3 segundos
    LaunchedEffect(mostrarMensajeAgregado) {
        if (mostrarMensajeAgregado) {
            kotlinx.coroutines.delay(3000)
            mostrarMensajeAgregado = false
        }
    }
}

@Composable
private fun CalificacionEstrellas(
    calificacion: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < calificacion.toInt())
                    Icons.Default.Star
                else
                    Icons.Default.Star,
                contentDescription = null,
                tint = if (index < calificacion.toInt())
                    AmarilloAji
                else
                    Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ResenaItem(resena: ProductoResena) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = resena.usuarioNombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MoradoAji
                )
                CalificacionEstrellas(resena.calificacion.toFloat())
            }

            Text(
                text = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(resena.fecha)),
                style = MaterialTheme.typography.bodySmall,
                color = MoradoAji.copy(alpha = 0.6f)
            )
        }

        Text(
            text = resena.comentario,
            style = MaterialTheme.typography.bodyMedium,
            color = MoradoAji
        )

        // Imagen de la reseña si existe
        if (resena.imagenUrl != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = Uri.parse(resena.imagenUrl),
                    contentDescription = "Imagen de reseña",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = com.example.appajicolorgrupo4.R.drawable.logo_principal)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoAgregarResena(
    productoId: String,
    onDismiss: () -> Unit,
    onResenaAgregada: (ProductoResena) -> Unit
) {
    val context = LocalContext.current
    var calificacion by remember { mutableStateOf(5) }
    var comentario by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    // Función para crear archivo temporal de imagen
    fun createTempImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(context.cacheDir, "reviews").apply {
            if(!exists()) mkdirs()
        }
        return File(storageDir, "REVIEW_${timeStamp}.jpg")
    }

    // Función para obtener URI usando FileProvider
    fun getImageUriForFile(file: File): Uri {
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    // Launcher para galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imagenUri = uri
        }
    }

    // Launcher para cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCaptureUri != null) {
            imagenUri = pendingCaptureUri
        }
        pendingCaptureUri = null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Escribe tu Reseña",
                color = MoradoAji,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de calificación
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Calificación:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MoradoAji,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { estrella ->
                            IconButton(
                                onClick = { calificacion = estrella },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$estrella estrellas",
                                    tint = if (estrella <= calificacion) AmarilloAji else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // Campo de comentario
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentario", color = MoradoAji) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    placeholder = { Text("Comparte tu experiencia con este producto", color = MoradoAji.copy(alpha = 0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.75f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.75f),
                        focusedBorderColor = MoradoAji,
                        unfocusedBorderColor = MoradoAji.copy(alpha = 0.5f),
                        focusedTextColor = MoradoAji,
                        unfocusedTextColor = MoradoAji
                    )
                )

                // Botones de imagen
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Agregar foto (opcional):",
                        style = MaterialTheme.typography.titleSmall,
                        color = MoradoAji,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MoradoAji
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MoradoAji)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = MoradoAji)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Galería", color = MoradoAji)
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    val file = createTempImageFile()
                                    val uri = getImageUriForFile(file)
                                    pendingCaptureUri = uri
                                    cameraLauncher.launch(uri)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Error al abrir cámara: ${e.message}",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MoradoAji
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MoradoAji)
                        ) {
                            Text("📷 Cámara", color = MoradoAji)
                        }
                    }

                    // Preview de la imagen seleccionada
                    if (imagenUri != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.75f)
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = imagenUri,
                                    contentDescription = "Imagen de reseña",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Botón para eliminar imagen
                                IconButton(
                                    onClick = { imagenUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Eliminar imagen",
                                        tint = MoradoAji
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val resena = ProductoResena(
                        id = "resena_${System.currentTimeMillis()}",
                        productoId = productoId,
                        usuarioNombre = "Usuario", // En producción vendría del perfil
                        calificacion = calificacion,
                        comentario = comentario,
                        imagenUrl = imagenUri?.toString()
                    )
                    onResenaAgregada(resena)
                },
                enabled = comentario.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmarilloAji,
                    contentColor = MoradoAji,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, MoradoAji)
            ) {
                Text("Publicar", color = MoradoAji, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MoradoAji
                )
            ) {
                Text("Cancelar", color = MoradoAji)
            }
        },
        containerColor = Color.White.copy(alpha = 0.95f)
    )
}

