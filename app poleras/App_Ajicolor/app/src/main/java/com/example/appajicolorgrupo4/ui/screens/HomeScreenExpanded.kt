package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenExpandedPreviewContent() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Polera Store - Desktop") })
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = Screen.Home.route
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Catálogo completo")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Carrito de compras")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Perfil de usuario")
            }
        }
    }
}
@Preview(name = "Expanded", widthDp = 1280, heightDp = 800, showBackground = true)
@Composable
fun PreviewExpanded() {
    HomeScreenExpandedPreviewContent()
}
