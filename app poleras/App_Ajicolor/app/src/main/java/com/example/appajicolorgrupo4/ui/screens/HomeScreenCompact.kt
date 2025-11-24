package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenCompact() { // Implementación de la pantalla de inicio para pantallas compactas
    val navController = rememberNavController()

    Scaffold(
        topBar = {// Usamos TopAppBar (ExperimentalMaterial3Api) → requiere @OptIn / requiere @OptIn(ExperimentalMaterial3Api) porque aún no es estable en Material3

            TopAppBar(title = { Text(text = "AJI DE COLOR") }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = Screen.Home.route
            )
        }
    ) { innerPadding ->
        // Contenido de la pantalla de inicio
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Bienvenido a Poleras AJI DE COLOR",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = { /* acción futura */ }) {
                Text(text = "Explorar Nuestros Productos")
            }
            Image(
                painter = painterResource(id = R.drawable.logo_principal),
                contentDescription = "Logo App",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(name ="Compact", widthDp = 360, heightDp = 800)
@Composable
fun PreviewCompact(){
    HomeScreenCompactPreviewContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenCompactPreviewContent() { // Implementación de la pantalla de inicio para pantallas compactas (solo preview)
    Scaffold(
        topBar = {// Usamos TopAppBar (ExperimentalMaterial3Api) → requiere @OptIn / requiere @OptIn(ExperimentalMaterial3Api) porque aún no es estable en Material3

            TopAppBar(title = { Text(text = "AJI DE COLOR") }
            )
        }
    ) { innerPadding ->
        // Contenido de la pantalla de inicio
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Bienvenido a Poleras AJI DE COLOR",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = { /* acción futura */ }) {
                Text(text = "Explorar Nuestros Productos")
            }
            Image(
                painter = painterResource(id = R.drawable.logo_principal),
                contentDescription = "Logo App",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}