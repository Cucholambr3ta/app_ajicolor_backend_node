package com.example.appajicolorgrupo4.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appajicolorgrupo4.R
import com.example.appajicolorgrupo4.navigation.Screen
import com.example.appajicolorgrupo4.ui.components.AppBackground
import com.example.appajicolorgrupo4.ui.components.AppNavigationDrawer
import com.example.appajicolorgrupo4.ui.components.BottomNavigationBar
import com.example.appajicolorgrupo4.ui.components.ProfileImageSelector
import com.example.appajicolorgrupo4.ui.components.TopBarWithCart
import com.example.appajicolorgrupo4.viewmodel.MainViewModel
import com.example.appajicolorgrupo4.viewmodel.UsuarioViewModel
import com.example.appajicolorgrupo4.ui.theme.AmarilloAji
import com.example.appajicolorgrupo4.ui.theme.MoradoAji
import com.example.appajicolorgrupo4.ui.theme.RojoAji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    // Cargar perfil al entrar
    LaunchedEffect(Unit) {
        usuarioViewModel.cargarPerfil()
    }

    val currentUser by usuarioViewModel.currentUser.collectAsState()
    val estado by usuarioViewModel.estado.collectAsState()
    val isEditMode by usuarioViewModel.isEditMode.collectAsState()
    val updateResultado by usuarioViewModel.updateResultado.collectAsState()
    val profileImageUri by usuarioViewModel.profileImageUri.collectAsState()

    // Estado para el drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Mostrar mensajes de resultado
    LaunchedEffect(updateResultado) {
        if (updateResultado != null) {
            kotlinx.coroutines.delay(3000)
            usuarioViewModel.limpiarMensajeActualizacion()
        }
    }


    AppBackground {
        AppNavigationDrawer(
            navController = navController,
            currentRoute = Screen.Profile.route,
            drawerState = drawerState
        ) {
            Scaffold(
                topBar = {
                    TopBarWithCart(
                        title = "Mi Perfil",
                        navController = navController,
                        drawerState = drawerState,
                        scope = scope
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        navController = navController,
                        currentRoute = Screen.Profile.route
                    )
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentUser == null) {
                        Text(
                            text = "No hay sesión activa",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        // Foto de perfil con selector
                        ProfileImageSelector(
                            defaultImageRes = R.drawable.profile,
                            onImageSelected = { uri ->
                                // Guardar la URI en el ViewModel para persistencia
                                usuarioViewModel.guardarFotoPerfil(uri)
                            },
                            currentImageUri = profileImageUri
                        )

                        Spacer(Modifier.height(8.dp))

                        // Texto indicativo
                        Text(
                            text = "Toca para cambiar foto",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Información Personal",
                            style = MaterialTheme.typography.headlineSmall,
                            color = AmarilloAji
                        )

                        Spacer(Modifier.height(8.dp))

                        // Mostrar mensaje de resultado
                        updateResultado?.let { mensaje ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (mensaje.contains("exitosamente"))
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = mensaje,
                                    modifier = Modifier.padding(12.dp),
                                    color = AmarilloAji
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        // Campo Nombre
                        OutlinedTextField(
                            value = estado.nombre,
                            onValueChange = { if (isEditMode) usuarioViewModel.actualizaNombre(it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditMode,
                            isError = estado.errores.nombre != null,
                            supportingText = {
                                estado.errores.nombre?.let { Text(it, color = AmarilloAji) }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmarilloAji,
                                unfocusedBorderColor = AmarilloAji,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = AmarilloAji,
                                unfocusedLabelColor = AmarilloAji,
                                cursorColor = AmarilloAji,
                                focusedTextColor = MoradoAji,
                                unfocusedTextColor = MoradoAji,
                                disabledTextColor = MoradoAji,
                                focusedContainerColor = Color.White.copy(alpha = 0.75f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.75f),
                                disabledContainerColor = Color.White.copy(alpha = 0.75f)
                            )
                        )

                        // Campo Correo
                        OutlinedTextField(
                            value = estado.correo,
                            onValueChange = { if (isEditMode) usuarioViewModel.actualizaCorreo(it) },
                            label = { Text("Correo electrónico") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditMode,
                            isError = estado.errores.correo != null,
                            supportingText = {
                                estado.errores.correo?.let { Text(it, color = AmarilloAji) }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmarilloAji,
                                unfocusedBorderColor = AmarilloAji,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = AmarilloAji,
                                unfocusedLabelColor = AmarilloAji,
                                cursorColor = AmarilloAji,
                                focusedTextColor = MoradoAji,
                                unfocusedTextColor = MoradoAji,
                                disabledTextColor = MoradoAji,
                                focusedContainerColor = Color.White.copy(alpha = 0.75f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.75f),
                                disabledContainerColor = Color.White.copy(alpha = 0.75f)
                            )
                        )

                        // Campo Teléfono
                        OutlinedTextField(
                            value = estado.telefono,
                            onValueChange = { if (isEditMode) usuarioViewModel.actualizaTelefono(it) },
                            label = { Text("Teléfono") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditMode,
                            isError = estado.errores.telefono != null,
                            supportingText = {
                                estado.errores.telefono?.let { Text(it, color = AmarilloAji) }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmarilloAji,
                                unfocusedBorderColor = AmarilloAji,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = AmarilloAji,
                                unfocusedLabelColor = AmarilloAji,
                                cursorColor = AmarilloAji,
                                focusedTextColor = MoradoAji,
                                unfocusedTextColor = MoradoAji,
                                disabledTextColor = MoradoAji,
                                focusedContainerColor = Color.White.copy(alpha = 0.75f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.75f),
                                disabledContainerColor = Color.White.copy(alpha = 0.75f)
                            )
                        )

                        // Campo Dirección
                        OutlinedTextField(
                            value = estado.direccion,
                            onValueChange = { if (isEditMode) usuarioViewModel.actualizaDireccion(it) },
                            label = { Text("Dirección") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditMode,
                            isError = estado.errores.direccion != null,
                            supportingText = {
                                estado.errores.direccion?.let { Text(it, color = AmarilloAji) }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmarilloAji,
                                unfocusedBorderColor = AmarilloAji,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = AmarilloAji,
                                unfocusedLabelColor = AmarilloAji,
                                cursorColor = AmarilloAji,
                                focusedTextColor = MoradoAji,
                                unfocusedTextColor = MoradoAji,
                                disabledTextColor = MoradoAji,
                                focusedContainerColor = Color.White.copy(alpha = 0.75f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.75f),
                                disabledContainerColor = Color.White.copy(alpha = 0.75f)
                            )
                        )


                        // Botones de edición
                        // Botones según el modo
                        if (!isEditMode) {
                            // Botón Modificar
                            Button(
                                onClick = { usuarioViewModel.activarEdicion() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmarilloAji,
                                    contentColor = MoradoAji
                                ),
                                border = androidx.compose.foundation.BorderStroke(2.dp, MoradoAji)
                            ) {
                                Text("Modificar Datos")
                            }
                        } else {
                            // Botones Guardar y Cancelar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { usuarioViewModel.cancelarEdicion() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        usuarioViewModel.guardarCambiosPerfil {
                                            // Acción después de guardar exitosamente
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Guardar")
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        HorizontalDivider()

                        Spacer(Modifier.height(16.dp))

                        // Botón Cerrar Sesión
                        Button(
                            onClick = {
                                usuarioViewModel.cerrarSesion()
                                // Navegar a StartScreen y limpiar el backstack completo
                                navController.navigate(Screen.StartScreen.route) {
                                    // Limpiar todo el backstack
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RojoAji,
                                contentColor = AmarilloAji
                            ),
                            border = androidx.compose.foundation.BorderStroke(2.dp, AmarilloAji)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                modifier = Modifier.size(20.dp),
                                tint = AmarilloAji
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Cerrar Sesión", color = AmarilloAji)
                        }
                    }
                }
            }
        }
    }
}
