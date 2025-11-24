package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.data.AccionNotificacion
import com.example.appajicolorgrupo4.data.Notificacion
import com.example.appajicolorgrupo4.data.TipoNotificacion
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.NotificacionesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    notificacionesViewModel: NotificacionesViewModel
) {
    val notificaciones by notificacionesViewModel.notificaciones.collectAsState()
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

    AppNavigationDrawer(
        navController = navController,
        drawerState = drawerState,
        currentRoute = currentRoute
    ) {
        AppBackground {
            Scaffold(
                topBar = {
                    TopBarWithCart(
                        title = "Notificaciones",
                        navController = navController,
                        drawerState = drawerState,
                        scope = scope,
                        additionalActions = {
                            if (notificaciones.isNotEmpty()) {
                                // Botón para marcar todas como leídas
                                IconButton(onClick = { notificacionesViewModel.marcarTodasComoLeidas() }) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Marcar todas como leídas"
                                    )
                                }
                                // Botón para eliminar todas las leídas
                                IconButton(onClick = { notificacionesViewModel.eliminarNotificacionesLeidas() }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar leídas"
                                    )
                                }
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
                containerColor = androidx.compose.ui.graphics.Color.Transparent
            ) { paddingValues ->
            if (notificaciones.isEmpty()) {
                // Estado vacío
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Sin notificaciones",
                        modifier = Modifier.size(80.dp),
                        tint = MoradoAji
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes notificaciones",
                        style = MaterialTheme.typography.titleLarge,
                        color = MoradoAji
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Las notificaciones sobre tus pedidos aparecerán aquí",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MoradoAji
                    )
                }
            } else {
                // Lista de notificaciones
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notificaciones, key = { it.id }) { notificacion ->
                        NotificacionCard(
                            notificacion = notificacion,
                            formatoFecha = formatoFecha,
                            onClick = {
                                // Marcar como leída
                                notificacionesViewModel.marcarComoLeida(notificacion.id)

                                // Ejecutar acción
                                when (val accion = notificacion.accion) {
                                    is AccionNotificacion.VerPedido -> {
                                        // Eliminar la notificación después de navegar
                                        notificacionesViewModel.eliminarNotificacion(notificacion.id)
                                        navController.navigate("detalle_pedido/${accion.numeroPedido}")
                                    }
                                    is AccionNotificacion.Navegar -> {
                                        navController.navigate(accion.ruta)
                                    }
                                    is AccionNotificacion.Ninguna,
                                    null -> {
                                        // Solo marcar como leída
                                    }
                                }
                            },
                            onDelete = {
                                notificacionesViewModel.eliminarNotificacion(notificacion.id)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun NotificacionCard(
    notificacion: Notificacion,
    formatoFecha: SimpleDateFormat,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida)
                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono según tipo de notificación
            Icon(
                imageVector = when (notificacion.tipo) {
                    TipoNotificacion.COMPRA_EXITOSA -> Icons.Default.CheckCircle
                    TipoNotificacion.PEDIDO_CONFIRMADO -> Icons.Default.Check
                    TipoNotificacion.PEDIDO_ENVIADO -> Icons.Default.ShoppingCart
                    TipoNotificacion.PEDIDO_ENTREGADO -> Icons.Default.Done
                    TipoNotificacion.PROMOCION -> Icons.Default.Star
                    TipoNotificacion.GENERAL -> Icons.Default.Info
                },
                contentDescription = null,
                tint = when (notificacion.tipo) {
                    TipoNotificacion.COMPRA_EXITOSA -> MaterialTheme.colorScheme.primary
                    TipoNotificacion.PEDIDO_CONFIRMADO -> MaterialTheme.colorScheme.tertiary
                    TipoNotificacion.PEDIDO_ENVIADO -> MaterialTheme.colorScheme.secondary
                    TipoNotificacion.PEDIDO_ENTREGADO -> MaterialTheme.colorScheme.primary
                    TipoNotificacion.PROMOCION -> MaterialTheme.colorScheme.error
                    TipoNotificacion.GENERAL -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.size(32.dp)
            )

            // Contenido
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = notificacion.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.Bold
                )

                // Mensaje con número de pedido en negrita
                if (notificacion.numeroPedido != null) {
                    val mensaje = buildAnnotatedString {
                        val texto = notificacion.mensaje
                        val numeroPedido = notificacion.numeroPedido
                        val startIndex = texto.indexOf(numeroPedido)

                        if (startIndex != -1) {
                            // Texto antes del número
                            append(texto.substring(0, startIndex))
                            // Número en negrita
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(numeroPedido)
                            }
                            // Texto después del número
                            append(texto.substring(startIndex + numeroPedido.length))
                        } else {
                            append(texto)
                        }
                    }
                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = if (notificacion.leida) 0.6f else 1f
                        )
                    )
                } else {
                    Text(
                        text = notificacion.mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = if (notificacion.leida) 0.6f else 1f
                        )
                    )
                }

                Text(
                    text = formatoFecha.format(Date(notificacion.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Botón eliminar
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar notificación",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

