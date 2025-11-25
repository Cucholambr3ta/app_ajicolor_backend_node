package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.data.ProductoCarrito
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart
import com.example.appajicolorgrupo4.viewmodel.CarritoViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel
) {
    val productos by carritoViewModel.productos.collectAsState()
    val subtotal by carritoViewModel.subtotal.collectAsState()
    val iva by carritoViewModel.iva.collectAsState()
    val costoEnvio by carritoViewModel.costoEnvio.collectAsState()
    val total by carritoViewModel.total.collectAsState()
    val calificaEnvioGratis by carritoViewModel.calificaEnvioGratis.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

    val formatoMoneda = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply {
            maximumFractionDigits = 0
        }
    }

    AppBackground {
        AppNavigationDrawer(
            navController = navController,
            drawerState = drawerState,
            currentRoute = currentRoute
        ) {
            Scaffold(
                topBar = {
                    TopBarWithCart(
                        title = androidx.compose.ui.res.stringResource(R.string.carrito_titulo),
                        navController = navController,
                        drawerState = drawerState,
                        scope = scope
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                },
                containerColor = androidx.compose.ui.graphics.Color.Transparent
            ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (productos.isEmpty()) {
                    // Carrito vacío
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Imagen de carrito vacío
                        Image(
                            painter = painterResource(id = R.drawable.carrovacio),
                            contentDescription = androidx.compose.ui.res.stringResource(R.string.carrito_vacio_desc),
                            modifier = Modifier
                                .size(200.dp)
                                .padding(bottom = 24.dp),
                            contentScale = ContentScale.Fit
                        )

                        Text(
                            text = androidx.compose.ui.res.stringResource(R.string.carrito_vacio_mensaje),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { navController.navigate(Screen.Catalogo.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                                .height(56.dp)
                        ) {
                            Text(
                                text = androidx.compose.ui.res.stringResource(R.string.ver_catalogo),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                } else {
                    // Anuncio de envío gratis
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (calificaEnvioGratis)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            else
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if (calificaEnvioGratis) {
                                    Text(
                                        text = androidx.compose.ui.res.stringResource(R.string.envio_gratis_felicidades),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = androidx.compose.ui.res.stringResource(R.string.compra_supera, formatoMoneda.format(20000)),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else {
                                    Text(
                                        text = androidx.compose.ui.res.stringResource(R.string.envio_gratis_sobre, formatoMoneda.format(20000)),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de productos
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(productos) { producto ->
                            ProductoCarritoItem(
                                producto = producto,
                                formatoMoneda = formatoMoneda,
                                onCantidadChanged = { nuevaCantidad ->
                                    carritoViewModel.actualizarCantidad(producto, nuevaCantidad)
                                },
                                onEliminar = {
                                    carritoViewModel.eliminarProducto(producto)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Resumen de costos
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(androidx.compose.ui.res.stringResource(R.string.subtotal))
                                Text(formatoMoneda.format(subtotal))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(androidx.compose.ui.res.stringResource(R.string.iva))
                                Text(formatoMoneda.format(iva))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(androidx.compose.ui.res.stringResource(R.string.envio))
                                if (calificaEnvioGratis) {
                                    Text(
                                        text = androidx.compose.ui.res.stringResource(R.string.gratis),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(formatoMoneda.format(costoEnvio))
                                }
                            }
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = androidx.compose.ui.res.stringResource(R.string.total),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón proceder al pago
                    Button(
                        onClick = { navController.navigate("checkout") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = productos.isNotEmpty()
                    ) {
                        Text(
                            text = androidx.compose.ui.res.stringResource(R.string.proceder_pago),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun ProductoCarritoItem(
    producto: ProductoCarrito,
    formatoMoneda: NumberFormat,
    onCantidadChanged: (Int) -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (producto.imagenResId != null) {
                Image(
                    painter = painterResource(id = producto.imagenResId),
                    contentDescription = androidx.compose.ui.res.stringResource(R.string.imagen_de, producto.nombre),
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Información del producto
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (producto.talla != null) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(R.string.talla, producto.talla.displayName),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = androidx.compose.ui.res.stringResource(R.string.color, producto.color.nombre),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Control de cantidad
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { onCantidadChanged(producto.cantidad - 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text(
                                text = "−",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Text(
                            text = producto.cantidad.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.widthIn(min = 24.dp)
                        )

                        IconButton(
                            onClick = { onCantidadChanged(producto.cantidad + 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = androidx.compose.ui.res.stringResource(R.string.aumentar_cantidad),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Precio y botón eliminar
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = formatoMoneda.format(producto.subtotal()),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = onEliminar,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = androidx.compose.ui.res.stringResource(R.string.eliminar),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
