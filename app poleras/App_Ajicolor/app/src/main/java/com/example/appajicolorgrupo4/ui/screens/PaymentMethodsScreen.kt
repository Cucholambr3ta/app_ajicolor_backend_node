package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.GeneradorNumeroPedido
import com.example.appajicolorgrupo4.data.MetodoPago
import com.example.appajicolorgrupo4.data.PedidoCompleto
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.viewmodel.CarritoViewModel
import com.example.appajicolorgrupo4.viewmodel.pedidosViewModel
import com.example.appajicolorgrupo4.viewmodel.usuarioViewModel
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    pedidosViewModel: com.example.appajicolorgrupo4.viewmodel.PedidosViewModel,
    usuarioViewModel: com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel,
    direccionEnvio: String? = null,
    telefono: String? = null,
    notasAdicionales: String? = null
) {
    var metodoSeleccionado by remember { mutableStateOf<MetodoPago?>(null) }
    val scope = rememberCoroutineScope()

    val productos by carritoViewModel.productos.collectAsState()
    val subtotal by carritoViewModel.subtotal.collectAsState()
    val impuestos by carritoViewModel.iva.collectAsState()
    val costoEnvio by carritoViewModel.costoEnvio.collectAsState()
    val total by carritoViewModel.total.collectAsState()

    val currentUser by usuarioViewModel.currentUser.collectAsState()
    val nombreUsuario = currentUser?.nombre ?: "Usuario"

    // Cargar el perfil del usuario para asegurar que currentUser no sea nulo
    LaunchedEffect(Unit) {
        usuarioViewModel.cargarPerfil()
    }

    val metodosPago = listOf(MetodoPago.TARJETA_CREDITO, MetodoPago.TARJETA_DEBITO)

    val formatoMoneda = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply {
            maximumFractionDigits = 0
        }
    }

    val direccionDecodificada = remember(direccionEnvio) {
        URLDecoder.decode(direccionEnvio ?: "", "UTF-8")
    }
    val telefonoDecodificado = remember(telefono) {
        URLDecoder.decode(telefono ?: "", "UTF-8")
    }
    val notasDecodificadas = remember(notasAdicionales) {
        URLDecoder.decode(notasAdicionales ?: "", "UTF-8")
    }

    AppBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Método de Pago") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    )
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
                // Total a pagar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total a Pagar:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatoMoneda.format(total),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Selecciona tu método de pago:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de métodos de pago
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(metodosPago) { metodo ->
                        MetodoPagoItem(
                            metodo = metodo,
                            isSelected = metodoSeleccionado == metodo,
                            onClick = { metodoSeleccionado = metodo }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón confirmar pago
                Button(
                    onClick = {
                        val user = currentUser
                        if (metodoSeleccionado != null && user != null) {
                            scope.launch {
                                val numeroPedido = GeneradorNumeroPedido.generar(nombreUsuario)
                                val pedido = PedidoCompleto(
                                    numeroPedido = numeroPedido,
                                    nombreUsuario = nombreUsuario,
                                    productos = productos,
                                    subtotal = subtotal.toDouble(),
                                    impuestos = impuestos.toDouble(),
                                    costoEnvio = costoEnvio.toDouble(),
                                    total = total.toDouble(),
                                    direccionEnvio = direccionDecodificada,
                                    telefono = telefonoDecodificado,
                                    notasAdicionales = notasDecodificadas,
                                    metodoPago = metodoSeleccionado!!,
                                    estado = EstadoPedido.CONFIRMADO,
                                    fechaCreacion = System.currentTimeMillis(),
                                    fechaConfirmacion = System.currentTimeMillis()
                                )

                                val resultado = pedidosViewModel.agregarPedido(pedido, user.id)
                                resultado.onSuccess {
                                    carritoViewModel.limpiarCarrito()
                                    navController.navigate(Screen.Success.createRoute(numeroPedido)) {
                                        popUpTo(Screen.Cart.route) { inclusive = true }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = metodoSeleccionado != null && currentUser != null
                ) {
                    Text(
                        text = if (metodoSeleccionado != null)
                            "Confirmar Pago"
                        else
                            "Selecciona un Método de Pago",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun MetodoPagoItem(
    metodo: MetodoPago,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            else
                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        border = if (isSelected)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del método de pago
            Text(
                text = metodo.icono,
                style = MaterialTheme.typography.headlineMedium
            )

            // Nombre del método
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = metodo.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = obtenerDescripcionMetodo(metodo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Indicador de selección
            if (isSelected) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Obtiene la descripción de cada método de pago
 */
private fun obtenerDescripcionMetodo(metodo: MetodoPago): String {
    return when (metodo) {
        MetodoPago.TARJETA_CREDITO -> "Visa, Mastercard, American Express"
        MetodoPago.TARJETA_DEBITO -> "Tarjetas de débito bancarias"
        else -> ""
    }
}
