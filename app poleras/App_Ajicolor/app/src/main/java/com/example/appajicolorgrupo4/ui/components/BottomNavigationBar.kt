package com.example.appajicolorgrupo4.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.navigation.Screen

/**
 * Barra de navegación inferior compartida para todas las pantallas principales
 * después de iniciar sesión.
 * Orden: Catálogo | Notificaciones | Inicio | Perfil | Compras
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String
) {
    NavigationBar(
        containerColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.25f)
    ) {
        // 1. Catálogo de Productos
        NavigationBarItem(
            selected = currentRoute == Screen.Catalogo.route,
            onClick = {
                if (currentRoute != Screen.Catalogo.route) {
                    navController.navigate(Screen.Catalogo.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Catálogo") },
            label = { Text("Catálogo") }
        )

        // 2. Notificaciones
        NavigationBarItem(
            selected = currentRoute == Screen.Notification.route,
            onClick = {
                if (currentRoute != Screen.Notification.route) {
                    navController.navigate(Screen.Notification.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(imageVector = Icons.Filled.Notifications, contentDescription = "Notificaciones") },
            label = { Text("Alertas") }
        )

        // 3. Inicio
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = {
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
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )

        // 4. Perfil
        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route,
            onClick = {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(imageVector = Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )

        // 5. Compras (Historial)
        NavigationBarItem(
            selected = currentRoute == Screen.OrderHistory.route,
            onClick = {
                if (currentRoute != Screen.OrderHistory.route) {
                    navController.navigate(Screen.OrderHistory.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "Compras") },
            label = { Text("Compras") }
        )
    }
}

