package com.example.appajicolorgrupo4.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.navigation.Screen
import kotlinx.coroutines.launch

/**
 * Navigation Drawer compartido para todas las pantallas principales
 * después de iniciar sesión
 */
@Composable
fun AppNavigationDrawer(
    navController: NavController,
    currentRoute: String,
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.95f)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Menú de Navegación",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Opción Inicio
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = currentRoute == Screen.Home.route,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentRoute != Screen.Home.route) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) {
                                    inclusive = false
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción Perfil
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = currentRoute == Screen.Profile.route,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentRoute != Screen.Profile.route) {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción Ajustes
                NavigationDrawerItem(
                    label = { Text("Ajustes") },
                    selected = currentRoute == Screen.Settings.route,
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentRoute != Screen.Settings.route) {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Opción Notificaciones
                NavigationDrawerItem(
                    label = { Text("Notificaciones") },
                    selected = currentRoute == Screen.Notification.route,
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Notificaciones") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentRoute != Screen.Notification.route) {
                            navController.navigate(Screen.Notification.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción Carrito
                NavigationDrawerItem(
                    label = { Text("Carrito de Compras") },
                    selected = currentRoute == Screen.Cart.route,
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentRoute != Screen.Cart.route) {
                            navController.navigate(Screen.Cart.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción Historial de Pedidos
                NavigationDrawerItem(
                    label = { Text("Historial de Pedidos") },
                    selected = currentRoute == Screen.OrderHistory.route,
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Compras") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentRoute != Screen.OrderHistory.route) {
                            navController.navigate(Screen.OrderHistory.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        },
        content = content
    )
}
