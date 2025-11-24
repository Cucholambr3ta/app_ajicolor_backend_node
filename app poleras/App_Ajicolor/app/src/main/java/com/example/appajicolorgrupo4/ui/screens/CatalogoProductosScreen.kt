package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.data.CatalogoProductos
import com.example.appajicolorgrupo4.data.CategoriaProducto
import com.example.appajicolorgrupo4.data.Producto
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoProductosScreen(
    navController: NavController
) {
    val productos = remember { CatalogoProductos.obtenerTodos() }
    var categoriaFiltro by remember { mutableStateOf<CategoriaProducto?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

    // Filtrar productos por búsqueda y categoría
    val productosFiltrados = remember(categoriaFiltro, searchQuery) {
        var filtrados = productos

        // Filtrar por categoría
        if (categoriaFiltro != null) {
            filtrados = filtrados.filter { it.categoria == categoriaFiltro }
        }

        // Filtrar por búsqueda
        if (searchQuery.isNotBlank()) {
            filtrados = filtrados.filter { producto ->
                producto.nombre.contains(searchQuery, ignoreCase = true) ||
                producto.descripcion.contains(searchQuery, ignoreCase = true) ||
                producto.categoria.displayName.contains(searchQuery, ignoreCase = true)
            }
        }

        filtrados
    }

    AppBackground {
        AppNavigationDrawer(
            navController = navController,
            drawerState = drawerState,
            currentRoute = currentRoute
        ) {
            Scaffold(
                topBar = {
                    if (isSearchActive) {
                        // SearchBar cuando está activo
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { isSearchActive = false },
                            active = true,
                            onActiveChange = {
                                isSearchActive = it
                                if (!it) searchQuery = ""
                            },
                            placeholder = { Text("Buscar productos...") },
                            leadingIcon = {
                                IconButton(onClick = {
                                    isSearchActive = false
                                    searchQuery = ""
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Volver"
                                    )
                                }
                            },
                            trailingIcon = {
                                Row {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Limpiar búsqueda"
                                            )
                                        }
                                    }
                                    // Ícono del carrito también en el SearchBar
                                    IconButton(
                                        onClick = {
                                            navController.navigate(Screen.Cart.route) {
                                                launchSingleTop = true
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = "Carrito de compras"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                        // Sugerencias de búsqueda
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Mostrar productos que coincidan con la búsqueda
                            items(productosFiltrados) { producto ->
                                ListItem(
                                    headlineContent = { Text(producto.nombre) },
                                    supportingContent = {
                                        Text("${producto.categoria.displayName} • ${producto.precioFormateado()}")
                                    },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            isSearchActive = false
                                            navController.navigate(Screen.DetalleProducto.createRoute(producto.id))
                                        }
                                )
                            }

                            // Mensaje si no hay resultados
                            if (productosFiltrados.isEmpty() && searchQuery.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "No se encontraron productos con \"$searchQuery\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // TopBar normal con botón de búsqueda y carrito
                    TopBarWithCart(
                        title = "Catálogo de Productos",
                        navController = navController,
                        drawerState = drawerState,
                        scope = scope,
                        additionalActions = {
                            // Botón de búsqueda
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar productos"
                                )
                            }
                        }
                    )
                }
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
            ) {
                // Filtro de categorías
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = categoriaFiltro == null,
                            onClick = { categoriaFiltro = null },
                            label = { Text("Todos") }
                        )
                    }

                    items(CategoriaProducto.entries.toList()) { categoria ->
                        FilterChip(
                            selected = categoriaFiltro == categoria,
                            onClick = { categoriaFiltro = categoria },
                            label = { Text(categoria.displayName) }
                        )
                    }
                }

                // Grid de productos
                if (productosFiltrados.isEmpty()) {
                    // Mensaje cuando no hay productos
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🔍",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "No se encontraron productos con \"$searchQuery\""
                            } else {
                                "No hay productos en esta categoría"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(productosFiltrados) { producto ->
                            ProductoCard(
                                producto = producto,
                                onClick = { navController.navigate(Screen.DetalleProducto.createRoute(producto.id)) }
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Column {
            // Imagen
            Image(
                painter = painterResource(id = producto.imagenResId),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Nombre
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Calificación
                if (producto.numeroResenas > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${producto.calificacionPromedio}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Precio
                Text(
                    text = producto.precioFormateado(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Badge de categoría
                Text(
                    text = producto.categoria.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

