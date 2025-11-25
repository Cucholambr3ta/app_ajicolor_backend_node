package com.example.appajicolorgrupo4.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appajicolorgrupo4.ui.screens.*
import com.example.appajicolorgrupo4.viewmodel.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val carritoViewModel: CarritoViewModel = viewModel()
    val productoViewModel: ProductoViewModel = viewModel()
    val pedidosViewModel: PedidosViewModel = viewModel()
    val notificacionesViewModel: NotificacionesViewModel = viewModel()
val postViewModel: PostViewModel = viewModel()


    // Observar eventos de navegación del MainViewModel
    LaunchedEffect(key1 = true) {
        mainViewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route) {
                        event.popUpToRoute?.let { popUpTo ->
                            popUpTo(popUpTo.route) {
                                inclusive = event.inclusive
                            }
                        }
                        launchSingleTop = event.singleTop
                    }
                }
                is NavigationEvent.PopBackStack -> {
                    navController.popBackStack()
                }
                is NavigationEvent.NavigateUp -> {
                    navController.navigateUp()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.StartScreen.route
    ) {
        // Autenticación
        composable(Screen.StartScreen.route) { StartScreen(navController) }
        composable(Screen.Init.route) { InitScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController, usuarioViewModel) }
        composable(Screen.Registro.route) { RegistroScreen(navController, mainViewModel, usuarioViewModel) }
        composable(Screen.PasswordRecovery.route) { PasswordRecoveryScreen(navController) }

        // Principal
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                viewModel = mainViewModel,
                usuarioViewModel = usuarioViewModel
            )
        }
composable(Screen.Profile.route) { ProfileScreen(navController, mainViewModel, usuarioViewModel) }

composable(Screen.Settings.route) { SettingScreen(navController, mainViewModel) }

        composable(Screen.Notification.route) { NotificationScreen(navController, notificacionesViewModel) }

        // Comercio
        composable(Screen.Cart.route) { CartScreen(navController, carritoViewModel) }
        composable(Screen.Checkout.route) { CheckoutScreen(navController, carritoViewModel, usuarioViewModel) }
        
        composable(
            route = "${Screen.PaymentMethods.route}?direccion={direccion}&telefono={telefono}&notas={notas}",
            arguments = listOf(
                navArgument("direccion") { type = NavType.StringType; nullable = true },
                navArgument("telefono") { type = NavType.StringType; nullable = true },
                navArgument("notas") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val direccion = backStackEntry.arguments?.getString("direccion")
            val telefono = backStackEntry.arguments?.getString("telefono")
            val notas = backStackEntry.arguments?.getString("notas")
            
            PaymentMethodsScreen(
                navController = navController,
                carritoViewModel = carritoViewModel,
                pedidosViewModel = pedidosViewModel,
                usuarioViewModel = usuarioViewModel,
                direccionEnvio = direccion,
                telefono = telefono,
                notasAdicionales = notas
            )
        }
        // Fallback para sin argumentos
        composable(Screen.PaymentMethods.route) { 
             PaymentMethodsScreen(
                navController = navController,
                carritoViewModel = carritoViewModel,
                pedidosViewModel = pedidosViewModel,
                usuarioViewModel = usuarioViewModel
            )
        }

composable(Screen.Catalogo.route) { CatalogoProductosScreen(navController) }


        // Pedidos
        composable(Screen.OrderHistory.route) { OrderHistoryScreen(navController) }

        // Depuración
        composable(Screen.Debug.route) { DebugScreen(navController, usuarioViewModel) }

        // Admin
composable(Screen.Posts.route) { PostScreen(postViewModel) }


        // Arguments
        composable(
            route = Screen.DetalleProducto.routePattern,
            arguments = listOf(navArgument("productoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId")
            if (productoId != null) {
                DetalleProductoScreen(
                    productoId = productoId,
                    navController = navController,
                    productoViewModel = productoViewModel,
                    carritoViewModel = carritoViewModel
                )
            }
        }

        composable(
            route = Screen.DetallePedido.routePattern,
            arguments = listOf(navArgument("numeroPedido") { type = NavType.StringType })
        ) { backStackEntry ->
            val numeroPedido = backStackEntry.arguments?.getString("numeroPedido")
            if (numeroPedido != null) {
                DetallePedidoScreen(navController = navController, numeroPedido = numeroPedido)
            }
        }

        composable(
            route = Screen.Success.routePattern,
            arguments = listOf(navArgument("numeroPedido") { type = NavType.StringType })
        ) { backStackEntry ->
            val numeroPedido = backStackEntry.arguments?.getString("numeroPedido")
            SuccessScreen(navController, numeroPedido)
        }
        
        // Old Detail route compatibility
        composable(
            route = "detail_page/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
             val itemId = backStackEntry.arguments?.getString("itemId")
             if (itemId != null) {
                 DetalleProductoScreen(
                    productoId = itemId,
                    navController = navController,
                    productoViewModel = productoViewModel,
                    carritoViewModel = carritoViewModel
                )
             }
        }
    }
}
