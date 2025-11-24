package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecoveryScreen(
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showSuccess) {
                // Imagen de envío de correo
                Image(
                    painter = painterResource(id = R.drawable.envio_correo),
                    contentDescription = "Correo Enviado",
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "✓ Correo Enviado",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AmarilloAji
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Revisa tu correo electrónico para restablecer tu contraseña.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Inicio de Sesión")
                }
            } else {
                // Imagen de recuperación
                Image(
                    painter = painterResource(id = R.drawable.recovery),
                    contentDescription = "Recuperar Contraseña",
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Recuperar Contraseña",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AmarilloAji
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ingresa tu correo electrónico y te enviaremos instrucciones para restablecer tu contraseña.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmarilloAji,
                        unfocusedBorderColor = AmarilloAji,
                        focusedLabelColor = AmarilloAji,
                        unfocusedLabelColor = AmarilloAji,
                        cursorColor = AmarilloAji,
                        focusedTextColor = MoradoAji,
                        unfocusedTextColor = MoradoAji,
                        focusedContainerColor = Color.White.copy(alpha = 0.75f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showSuccess = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.isNotBlank()
                ) {
                    Text("Enviar Instrucciones")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

