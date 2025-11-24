package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.CarouselProductosCompacto
import com.example.appajicolorgrupo4.ui.components.ProductoCarousel
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    AppBackground {
        HomeScreenCompact(navController, viewModel)
    }
}

/**
 * Versión Compacta (pantallas pequeñas, móviles)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenCompact(
    navController: NavController,
    viewModel: MainViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Lista de productos de ejemplo
    val productosDestacados = remember {
        listOf(
            ProductoCarousel(
                id = "prod_015", // ID correcto de Red Hot Chili Peppers
                nombre = "Polera Red Hot Chili Peppers",
                precio = "$15.000",
                imageResId = R.drawable.polera_red_hot_chili_peppers,
                categoria = "Serigrafía",
                descripcion = "Polera diseño Material: Algodón 100%"
            ),
            ProductoCarousel(
                id = "prod_005", // ID correcto de Faith No More
                nombre = "Polera Faith No More",
                precio = "$15.000",
                imageResId = R.drawable.polera_faith_no_more,
                categoria = "Serigrafía",
                descripcion = "Polera diseño Material: Algodón"
            ),
            ProductoCarousel(
                id = "prod_017", // ID correcto de System Of A Down
                nombre = "Polera System Of A Down",
                precio = "$15.000",
                imageResId = R.drawable.polera_system_of_a_down,
                categoria = "Serigrafía",
                descripcion = "Polera diseño Material: Algodón"
            ),
            ProductoCarousel(
                id = "prod_023", // ID correcto de Jockey
                nombre = "Jockey Genérico",
                precio = "$8.000",
                imageResId = R.drawable.jockey,
                categoria = "Accesorios",
                descripcion = "Jockey genérico"
            )
        )
    }

    val productosNuevos = remember {
        listOf(
            ProductoCarousel(
                id = "prod_021", // ID correcto de Tool
                nombre = "Polera Tool",
                precio = "$15.000",
                imageResId = R.drawable.polera_tool,
                categoria = "Serigrafía",
                descripcion = "Polera diseño Material: Algodón 100%"
            ),
            ProductoCarousel(
                id = "prod_008", // ID correcto de Incubus
                nombre = "Polera Incubus",
                precio = "$15.000",
                imageResId = R.drawable.polera_incubus,
                categoria = "Serigrafía",
                descripcion = "Polera diseño Material: Algodón"
            ),
            ProductoCarousel(
                id = "prod_004", // ID correcto de Deftones
                nombre = "Polera Deftones",
                precio = "$15.000",
                imageResId = R.drawable.polera_deftones,
                categoria = "Serigrafía",
                descripcion = "Polera diseño Material: Algodón 100%"
            ),
            ProductoCarousel(
                id = "prod_014", // ID correcto de Rage Against The Machine
                nombre = "Polera Rage Against The Machine",
                precio = "$15.000",
                imageResId = R.drawable.polera_rage_against_the_machine,
                categoria = "Serigrafía",
                descripcion = "Polera diseño Material: Algodón"
            )
        )
    }

    AppNavigationDrawer(
        navController = navController,
        currentRoute = Screen.Home.route,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopBarWithCart(
                    title = "Inicio",
                    navController = navController,
                    drawerState = drawerState,
                    scope = scope
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = Screen.Home.route
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Mensaje de bienvenida
                Text(
                    text = "¡Bienvenido a Aji Color!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AmarilloAji,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )

                // Carousel de productos destacados
                CarouselProductosCompacto(
                    productos = productosDestacados,
                    titulo = "Productos Destacados",
                    onProductClick = { producto ->
                        navController.navigate(Screen.DetalleProducto.createRoute(producto.id))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Carousel de nuevos productos
                CarouselProductosCompacto(
                    productos = productosNuevos,
                    titulo = "Nuevos Productos",
                    onProductClick = { producto ->
                        navController.navigate(Screen.DetalleProducto.createRoute(producto.id))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Carousel de recomendados (reutilizando los mismos datos)
                CarouselProductosCompacto(
                    productos = productosDestacados,
                    titulo = "Más Vendidos",
                    onProductClick = { producto ->
                        navController.navigate(Screen.DetalleProducto.createRoute(producto.id))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para ver todo el catálogo
                Button(
                    onClick = { navController.navigate(Screen.Catalogo.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmarilloAji,
                        contentColor = MoradoAji
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, MoradoAji)
                ) {
                    Text("Ver Todo el Catálogo", color = MoradoAji)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Versión Medium (tablets)
 */
@Composable
fun HomeScreenMedium(
    navController: NavController,
    viewModel: MainViewModel
) {
    // Por ahora puedes reutilizar la compacta
    HomeScreenCompact(navController, viewModel)
}

/**
 * Versión Expanded (pantallas grandes, escritorio)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenExpanded(
    navController: NavController,
    viewModel: MainViewModel
) {
    // Reutilizar la misma implementación que Compact
    // En el futuro se puede personalizar para pantallas grandes
    HomeScreenCompact(navController, viewModel)
}
