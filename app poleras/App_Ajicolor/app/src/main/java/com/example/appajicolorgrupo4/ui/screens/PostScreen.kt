package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.appajicolorgrupo4.data.CategoriaProducto
import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.PedidoCompleto
import com.example.appajicolorgrupo4.data.Producto
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.PedidosViewModel
import com.example.appajicolorgrupo4.viewmodel.ProductoViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    productoViewModel: ProductoViewModel,
    pedidosViewModel: PedidosViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Productos", "Pedidos")

    AppBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Panel de Administración", color = AmarilloAji) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MoradoAji,
                        titleContentColor = AmarilloAji
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MoradoAji,
                    contentColor = AmarilloAji
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> AdminProductosTab(productoViewModel)
                    1 -> AdminPedidosTab(pedidosViewModel)
                }
            }
        }
    }
}

@Composable
fun AdminProductosTab(viewModel: ProductoViewModel) {
    val productos by viewModel.productos.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var productoEditar by remember { mutableStateOf<Producto?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(productos) { producto ->
                ProductoAdminItem(
                    producto = producto,
                    onEdit = { 
                        productoEditar = producto
                        showDialog = true 
                    },
                    onDelete = { viewModel.eliminarProducto(producto.id) }
                )
            }
        }

        FloatingActionButton(
            onClick = { 
                productoEditar = null
                showDialog = true 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MoradoAji,
            contentColor = AmarilloAji
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
        }
    }

    if (showDialog) {
        ProductoDialog(
            producto = productoEditar,
            onDismiss = { showDialog = false },
            onConfirm = { nuevoProducto ->
                if (productoEditar == null) {
                    viewModel.crearProducto(nuevoProducto)
                } else {
                    viewModel.actualizarProducto(nuevoProducto)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun ProductoAdminItem(
    producto: Producto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, fontWeight = FontWeight.Bold)
                Text(text = producto.precioFormateado())
                Text(text = "Stock: ${producto.stock}")
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MoradoAji)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun ProductoDialog(
    producto: Producto?,
    onDismiss: () -> Unit,
    onConfirm: (Producto) -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(producto?.descripcion ?: "") }
    var precio by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var stock by remember { mutableStateOf(producto?.stock?.toString() ?: "") }
    var categoria by remember { mutableStateOf(producto?.categoria ?: CategoriaProducto.SERIGRAFIA) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (producto == null) "Nuevo Producto" else "Editar Producto",
                    style = MaterialTheme.typography.titleLarge,
                    color = MoradoAji
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") }
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio") }
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") }
                )
                
                // Categoría (Simplificado)
                Text("Categoría: ${categoria.name}")
                // Aquí se podría agregar un Dropdown para categoría

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(
                        onClick = {
                            val nuevoProducto = Producto(
                                id = producto?.id ?: UUID.randomUUID().toString(),
                                nombre = nombre,
                                descripcion = descripcion,
                                precio = precio.toIntOrNull() ?: 0,
                                stock = stock.toIntOrNull() ?: 0,
                                categoria = categoria,
                                imagenResId = 0, // Placeholder
                                imagenUrl = null // Placeholder
                            )
                            onConfirm(nuevoProducto)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MoradoAji)
                    ) {
                        Text("Guardar", color = AmarilloAji)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPedidosTab(viewModel: PedidosViewModel) {
    val pedidos by viewModel.pedidos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarTodosLosPedidos()
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(pedidos) { pedido ->
            PedidoAdminItem(
                pedido = pedido,
                onStatusChange = { nuevoEstado ->
                    viewModel.actualizarEstadoPedido(pedido.numeroPedido, nuevoEstado)
                }
            )
        }
    }
}

@Composable
fun PedidoAdminItem(
    pedido: PedidoCompleto,
    onStatusChange: (EstadoPedido) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Pedido #${pedido.numeroPedido}", fontWeight = FontWeight.Bold)
            Text(text = "Total: $${pedido.total}")
            Text(text = "Estado: ${pedido.estado}")
            
            Box {
                Button(onClick = { expanded = true }) {
                    Text("Cambiar Estado")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    EstadoPedido.values().forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado.name) },
                            onClick = {
                                onStatusChange(estado)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}