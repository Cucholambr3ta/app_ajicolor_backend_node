package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.PedidoCompleto
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.CustomDialog
import com.example.appajicolorgrupo4.ui.components.DetallePedidoDialogContent
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.pedidosViewModel
import com.example.appajicolorgrupo4.viewmodel.usuarioViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController
) {
    val pedidosViewModel = pedidosViewModel()
    val usuarioViewModel = usuarioViewModel()
    val todosPedidos by pedidosViewModel.pedidos.collectAsState()
    val currentUser by usuarioViewModel.currentUser.collectAsState()
    var estadoFiltro by remember { mutableStateOf<EstadoPedido?>(null) }
    var pedidoSeleccionado by remember { mutableStateOf<PedidoCompleto?>(null) }

    // Cargar pedidos del usuario desde SQLite
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            pedidosViewModel.cargarPedidosUsuario(user.id)
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

    val pedidosFiltrados = remember(todosPedidos, estadoFiltro) {
        if (estadoFiltro == null) {
            todosPedidos
        } else {
            todosPedidos.filter { it.estado == estadoFiltro }
        }
    }

    val formatoMoneda = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply {
            maximumFractionDigits = 0
        }
    }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    }

    if (pedidoSeleccionado != null) {
        CustomDialog(
            onDismissRequest = { pedidoSeleccionado = null },
            title = "Detalle del Pedido"
        ) {
            DetallePedidoDialogContent(pedido = pedidoSeleccionado!!, formatoMoneda = formatoMoneda, formatoFecha = formatoFecha)
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
                        title = "Mis Pedidos",
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
                containerColor = Color.Transparent
            ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Filtros por estado
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Filtro "Todos"
                    item {
                        FilterChip(
                            selected = estadoFiltro == null,
                            onClick = { estadoFiltro = null },
                            label = { Text("Todos (${todosPedidos.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmarilloAji,
                                selectedLabelColor = MoradoAji,
                                containerColor = Color.White.copy(alpha = 0.75f),
                                labelColor = MoradoAji
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = estadoFiltro == null,
                                borderColor = MoradoAji,
                                selectedBorderColor = MoradoAji,
                                borderWidth = 2.dp,
                                selectedBorderWidth = 2.dp
                            )
                        )
                    }

                    // Filtros por estado
                    items(EstadoPedido.entries.toList()) { estado ->
                        val cantidadEstado = todosPedidos.count { it.estado == estado }
                        FilterChip(
                            selected = estadoFiltro == estado,
                            onClick = { estadoFiltro = estado },
                            label = {
                                Text("${estado.displayName} ($cantidadEstado)")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmarilloAji,
                                selectedLabelColor = MoradoAji,
                                containerColor = Color.White.copy(alpha = 0.75f),
                                labelColor = MoradoAji
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = estadoFiltro == estado,
                                borderColor = MoradoAji,
                                selectedBorderColor = MoradoAji,
                                borderWidth = 2.dp,
                                selectedBorderWidth = 2.dp
                            )
                        )
                    }
                }

                // Lista de pedidos o mensaje vacío
                if (pedidosFiltrados.isEmpty()) {
                    // Sin pedidos
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📦",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (estadoFiltro == null)
                                "No tienes pedidos aún"
                            else
                                "No tienes pedidos en estado ${estadoFiltro!!.displayName}",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color = MoradoAji
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Realiza tu primera compra",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MoradoAji
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { navController.navigate(Screen.Catalogo.route) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ir al Catálogo")
                        }
                    }
                } else {
                    // Lista de pedidos
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pedidosFiltrados) { pedido ->
                            PedidoCard(
                                pedido = pedido,
                                formatoMoneda = formatoMoneda,
                                formatoFecha = formatoFecha,
                                onClick = {
                                    pedidoSeleccionado = pedido
                                }
                            )
                        }

                        // Espacio al final
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun PedidoCard(
    pedido: PedidoCompleto,
    formatoMoneda: NumberFormat,
    formatoFecha: SimpleDateFormat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Número de pedido y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pedido ${pedido.numeroPedido}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatoFecha.format(Date(pedido.fechaCreacion)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                EstadoBadge(pedido.estado)
            }

            HorizontalDivider()

            // Información resumida
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Items",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "${pedido.cantidadTotalItems()} productos",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = formatoMoneda.format(pedido.total),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Método",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = pedido.metodoPago.icono,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Progreso del pedido
            if (pedido.estado != EstadoPedido.ENTREGADO) {
                Column {
                    Text(
                        text = "Estado actual: ${pedido.estado.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (pedido.estado.ordinal + 1) / EstadoPedido.entries.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Pedido entregado",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Botón ver detalles
            TextButton(
                onClick = onClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Ver Detalles →")
            }
        }
    }
}

@Composable
private fun EstadoBadge(estado: EstadoPedido) {
    val (backgroundColor, textColor) = when (estado) {
        EstadoPedido.CONFIRMADO -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        EstadoPedido.PREPARANDO -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        EstadoPedido.ENVIADO -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        EstadoPedido.ENTREGADO -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = estado.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
