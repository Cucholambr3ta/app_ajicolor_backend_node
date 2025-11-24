package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel

@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val estado by usuarioViewModel.estado.collectAsState()
    val registroResultado by usuarioViewModel.registroResultado.collectAsState()

    var showPassword by remember { mutableStateOf(false) }
    var showPasswordConfirmation by remember { mutableStateOf(false) }

    // Diálogo de éxito o error
    registroResultado?.let {
        AlertDialog(
            onDismissRequest = { usuarioViewModel.limpiarMensajeRegistro() },
            title = { Text(if (it.contains("exitosamente")) "¡Registro Exitoso!" else "Error de Registro") },
            text = { Text(it) },
            confirmButton = {
                Button(
                    onClick = {
                        if (it.contains("exitosamente")) {
                            navController.navigate("login") {
                                popUpTo("registro") { inclusive = true }
                            }
                        }
                        usuarioViewModel.limpiarMensajeRegistro()
                    }
                ) {
                    Text(if (it.contains("exitosamente")) "Ir a Login" else "Entendido")
                }
            }
        )
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineLarge,
                color = AmarilloAji
            )
            Spacer(Modifier.height(16.dp))

            // Campo Nombre Completo
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = { usuarioViewModel.actualizaNombre(it) },
                label = { Text("Nombre Completo", color = MoradoAji) },
                modifier = Modifier.fillMaxWidth(),
                isError = estado.errores.nombre != null,
                supportingText = {
                    estado.errores.nombre?.let { Text(it, color = AmarilloAji) }
                },
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
            Spacer(Modifier.height(8.dp))

            // Campo Correo
            OutlinedTextField(
                value = estado.correo,
                onValueChange = { usuarioViewModel.actualizaCorreo(it) },
                label = { Text("Correo Electrónico", color = MoradoAji) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                isError = estado.errores.correo != null,
                supportingText = {
                    estado.errores.correo?.let { Text(it, color = AmarilloAji) }
                },
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
            Spacer(Modifier.height(8.dp))

            // Campo Teléfono
            OutlinedTextField(
                value = estado.telefono,
                onValueChange = { usuarioViewModel.actualizaTelefono(it) },
                label = { Text("Teléfono", color = MoradoAji) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                isError = estado.errores.telefono != null,
                supportingText = {
                    estado.errores.telefono?.let { Text(it, color = AmarilloAji) }
                },
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
            Spacer(Modifier.height(8.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = estado.clave,
                onValueChange = { usuarioViewModel.actualizaClave(it) },
                label = { Text("Contraseña", color = MoradoAji) },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = estado.errores.clave != null,
                supportingText = {
                    estado.errores.clave?.let { Text(it, color = AmarilloAji) }
                },
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
            Spacer(Modifier.height(8.dp))

            // Campo Confirmar Contraseña
            OutlinedTextField(
                value = estado.confirmarClave,
                onValueChange = { usuarioViewModel.actualizaConfirmarClave(it) },
                label = { Text("Confirmar Contraseña", color = MoradoAji) },
                visualTransformation = if (showPasswordConfirmation) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPasswordConfirmation = !showPasswordConfirmation }) {
                        Icon(
                            imageVector = if (showPasswordConfirmation) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password confirmation visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = estado.errores.confirmarClave != null,
                supportingText = {
                    estado.errores.confirmarClave?.let { Text(it, color = AmarilloAji) }
                },
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaTerminos,
                    onCheckedChange = { usuarioViewModel.actualizaAceptaTerminos(it) },
                    colors = CheckboxDefaults.colors(checkedColor = AmarilloAji)
                )
                Text("Acepto los términos y condiciones", color = MoradoAji)
            }
            estado.errores.aceptaTerminos?.let {
                Text(it, color = AmarilloAji, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))

            // Botón Registrarse
            Button(
                onClick = { usuarioViewModel.registrarUsuario { navController.navigate("login") } },
                modifier = Modifier.fillMaxWidth(),
                enabled = true, // Re-enable button
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmarilloAji,
                    contentColor = MoradoAji,
                    disabledContainerColor = AmarilloAji.copy(alpha = 0.5f)
                )
            ) {
                Text("Registrarse")
            }

            Spacer(Modifier.height(8.dp))

            // Botón para ir a Login
            TextButton(onClick = { navController.navigate("login") }) {
                Text("¿Ya tienes cuenta? Inicia Sesión", color = AmarilloAji)
            }
        }
    }
}
