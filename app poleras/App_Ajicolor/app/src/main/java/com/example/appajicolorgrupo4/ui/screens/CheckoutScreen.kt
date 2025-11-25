package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.data.* 
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.viewmodel.CarritoViewModel
import com.example.appajicolorgrupo4.viewmodel.PedidosViewModel
import com.example.appajicolorgrupo4.viewmodel.usuarioViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    usuarioViewModel: com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
) {
    val productos by carritoViewModel.productos.collectAsState()
    val subtotal by carritoViewModel.subtotal.collectAsState()
    val impuestos by carritoViewModel.iva.collectAsState()
    val costoEnvio by carritoViewModel.costoEnvio.collectAsState()
    val total by carritoViewModel.total.collectAsState()
    val calificaEnvioGratis by carritoViewModel.calificaEnvioGratis.collectAsState()

    val formatoMoneda = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply {
            maximumFractionDigits = 0
        }
    }

    val estadoUsuario by usuarioViewModel.estado.collectAsState()
    val currentUser by usuarioViewModel.currentUser.collectAsState()
    var notasAdicionales by remember { mutableStateOf("") }

    // Cargar el perfil del usuario para obtener la dirección y el teléfono
    LaunchedEffect(Unit) {
        usuarioViewModel.cargarPerfil()
    }

    AppBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Confirmar Pedido") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Resumen de productos
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Resumen del Pedido",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            HorizontalDivider()

                            productos.forEach { producto ->
                                ProductoResumenItem(producto, formatoMoneda)
                            }

                            HorizontalDivider()

                            // Desglose de costos
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subtotal:")
                                    Text(formatoMoneda.format(subtotal))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Impuestos (19% IVA):")
                                    Text(formatoMoneda.format(impuestos))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Envío:")
                                    if (calificaEnvioGratis) {
                                        Text(
                                            text = "GRATIS",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        Text(formatoMoneda.format(costoEnvio))
                                    }
                                }

                                if (calificaEnvioGratis) {
                                    Text(
                                        text = "✓ Envío gratis por compra sobre ${formatoMoneda.format(20000)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total a Pagar:",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formatoMoneda.format(total),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Información de envío
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Información de Envío",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = estadoUsuario.direccion,
                                onValueChange = { usuarioViewModel.actualizaDireccion(it) },
                                label = { Text("Dirección de Envío *") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                placeholder = { Text("Calle, número, distrito, ciudad") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White.copy(alpha = 0.75f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                                )
                            )

                            OutlinedTextField(
                                value = estadoUsuario.telefono,
                                onValueChange = { usuarioViewModel.actualizaTelefono(it) },
                                label = { Text("Teléfono de Contacto *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = { Text("999 999 999") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White.copy(alpha = 0.75f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                                )
                            )

                            OutlinedTextField(
                                value = notasAdicionales,
                                onValueChange = { notasAdicionales = it },
                                label = { Text("Notas Adicionales (Opcional)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                placeholder = { Text("Referencias, indicaciones especiales, etc.") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White.copy(alpha = 0.75f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                                )
                            )
                        }
                    }
                }

                // Botón continuar
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                val direccion = URLEncoder.encode(estadoUsuario.direccion, StandardCharsets.UTF_8.toString())
                                val telefono = URLEncoder.encode(estadoUsuario.telefono, StandardCharsets.UTF_8.toString())
                                val notas = URLEncoder.encode(notasAdicionales, StandardCharsets.UTF_8.toString())
                                navController.navigate("${Screen.PaymentMethods.route}?direccion=$direccion&telefono=$telefono&notas=$notas")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = estadoUsuario.direccion.isNotBlank() && estadoUsuario.telefono.isNotBlank() && productos.isNotEmpty() && currentUser != null
                        ) {
                            Text(
                                text = "Seleccionar Método de Pago",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        if (estadoUsuario.direccion.isBlank() || estadoUsuario.telefono.isBlank()) {
                            Text(
                                text = "* Complete todos los campos obligatorios",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}



@Composable
private fun ProductoResumenItem(
    producto: ProductoCarrito,
    formatoMoneda: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (producto.imagenResId != null) {
            Image(
                painter = painterResource(id = producto.imagenResId),
                contentDescription = "Imagen de ${producto.nombre}",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = buildString {
                    if (producto.talla != null) {
                        append("Talla ${producto.talla.displayName} • ")
                    }
                    append(producto.color.nombre)
                    append(" • Cant: ${producto.cantidad}")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Precio
        Text(
            text = formatoMoneda.format(producto.subtotal()),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
