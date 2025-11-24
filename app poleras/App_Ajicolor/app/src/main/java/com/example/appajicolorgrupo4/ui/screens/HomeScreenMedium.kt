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
fun HomeScreenMediumPreviewContent() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Polera Store - Tablet") })
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
                .padding(16.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Catálogo destacado")
                Button(onClick = { /* Acción */ }) {
                    Text("Explorar")
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Ofertas especiales")
                Button(onClick = { /* Acción */ }) {
                    Text("Ver más")
                }
            }
        }
    }
}

@Preview(name = "Medium", widthDp = 600, heightDp = 1024, showBackground = true)
@Composable
fun PreviewMedium() {
    HomeScreenMediumPreviewContent()
}
