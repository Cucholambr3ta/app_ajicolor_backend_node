package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.AuthViewModel
import com.example.appajicolorgrupo4.viewmodel.AuthViewModelFactory
import com.example.appajicolorgrupo4.data.local.database.AppDatabase
import com.example.appajicolorgrupo4.data.repository.UserRepository
import com.example.appajicolorgrupo4.data.session.SessionManager
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val repository = UserRepository(database.userDao())
    val sessionManager = SessionManager(context)
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository, sessionManager))

    val estado by viewModel.login.collectAsState()
    var showPassword by remember { mutableStateOf(false) }

    // Navegar a Home cuando login exitoso
    LaunchedEffect(estado.success) {
        if (estado.success) {
            if (estado.isAdmin) {
                navController.navigate("posts_screen") {
                    popUpTo(Screen.StartScreen.route) { inclusive = true }
                }
            } else {
                usuarioViewModel.cargarPerfil()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.StartScreen.route) { inclusive = true }
                }
            }
            viewModel.clearLoginResult()
        }
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineLarge,
                color = AmarilloAji
            )
            Spacer(Modifier.height(24.dp))

            // Campo Email
            OutlinedTextField(
                value = estado.correo,
                onValueChange = viewModel::onLoginEmailChange,
                label = { Text("Email", color = MoradoAji) },
                placeholder = { Text("Ingrese su email", color = MoradoAji) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmarilloAji,
                    unfocusedBorderColor = AmarilloAji,
                    focusedLabelColor = MoradoAji,
                    unfocusedLabelColor = MoradoAji,
                    cursorColor = AmarilloAji,
                    focusedTextColor = MoradoAji,
                    unfocusedTextColor = MoradoAji,
                    focusedContainerColor = Color.White.copy(alpha = 0.75f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                ),
                isError = estado.correoError != null,
                supportingText = {
                    estado.correoError?.let {
                        Text(it, color = AmarilloAji)
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            // Campo Password
            OutlinedTextField(
                value = estado.clave,
                onValueChange = viewModel::onLoginPassChange,
                label = { Text("Contraseña", color = MoradoAji) },
                placeholder = { Text("Ingrese su contraseña", color = MoradoAji) },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = AmarilloAji
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmarilloAji,
                    unfocusedBorderColor = AmarilloAji,
                    focusedLabelColor = MoradoAji,
                    unfocusedLabelColor = MoradoAji,
                    cursorColor = AmarilloAji,
                    focusedTextColor = MoradoAji,
                    unfocusedTextColor = MoradoAji,
                    focusedContainerColor = Color.White.copy(alpha = 0.75f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                )
            )

            Spacer(Modifier.height(16.dp))

            // Mostrar error de login
            estado.errorMsg?.let { error ->
                Text(
                    text = error,
                    color = AmarilloAji,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
            }

            // Botón Entrar
            Button(
                onClick = viewModel::submitLogin,
                modifier = Modifier.fillMaxWidth(),
                enabled = estado.canSubmit && !estado.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmarilloAji,
                    contentColor = MoradoAji,
                    disabledContainerColor = AmarilloAji.copy(alpha = 0.5f),
                    disabledContentColor = MoradoAji.copy(alpha = 0.5f)
                ),
                border = BorderStroke(2.dp, MoradoAji)
            ) {
                if (estado.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MoradoAji,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...")
                } else {
                    Text("Entrar")
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botón Ir a Registro
            OutlinedButton(
                onClick = {
                    navController.navigate("registro")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AmarilloAji,
                    contentColor = MoradoAji
                ),
                border = BorderStroke(1.dp, MoradoAji)
            ) {
                Text("Crear cuenta")
            }

            Spacer(Modifier.height(12.dp))

            // Botón Recuperar Contraseña
            TextButton(
                onClick = {
                    navController.navigate("password_recovery")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AmarilloAji
                )
            ) {
                Text("Recuperar contraseña")
            }
        }
    }
}
