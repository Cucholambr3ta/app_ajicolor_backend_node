package com.example.appajicolorgrupo4.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * TopBar compartida que incluye el botón de menú hamburguesa
 * y el ícono del carrito en el lado derecho
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithCart(
    title: String,
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    additionalActions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title, color = AmarilloAji) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menú"
                )
            }
        },
        actions = {
            // Acciones adicionales (como la lupa en catálogo)
            additionalActions()

            // Ícono del carrito siempre presente
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
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

