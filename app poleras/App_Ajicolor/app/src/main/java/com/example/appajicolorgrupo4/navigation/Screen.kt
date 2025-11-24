package com.example.appajicolorgrupo4.navigation

// Sealed class que define rutas tipo-safe para la navegación.
// Cada pantalla se representa como un objeto único.
// Si se requieren argumentos, se usa data class.
sealed class Screen(val route: String) {
    // Rutas de autenticación
    data object StartScreen : Screen(route = "start_page")
    data object Init : Screen(route = "init_page")
    data object Login : Screen(route = "login")
    data object Registro : Screen(route = "registro")
    data object PasswordRecovery : Screen(route = "password_recovery")

    // Rutas principales (después de login)
    data object Home : Screen(route = "home_page")
    data object Profile : Screen(route = "profile_page")
    data object Settings : Screen(route = "settings_page")
    data object Notification : Screen(route = "notification_page")

    // Rutas de comercio
    data object Cart : Screen(route = "cart_page")
    data object Checkout : Screen(route = "checkout")
    data object PaymentMethods : Screen(route = "payment_methods")
    data object Catalogo : Screen(route = "catalogo")

    // Rutas de pedidos
    data object OrderHistory : Screen(route = "order_history_page")

    // Rutas de depuración (solo desarrollo)
    data object Debug : Screen(route = "debug_page")

    // Rutas de administración
    data object Posts : Screen(route = "posts_screen")

    // Rutas con argumentos
    data class Success(val numeroPedido: String?) : Screen(route = "success/{numeroPedido}") {
        companion object {
            const val routePattern = "success/{numeroPedido}"
            fun createRoute(numeroPedido: String) = "success/$numeroPedido"
        }
    }

    data class DetalleProducto(val productoId: String) : Screen(route = "producto/{productoId}") {
        companion object {
            const val routePattern = "producto/{productoId}"
            fun createRoute(productoId: String) = "producto/$productoId"
        }
    }

    data class DetallePedido(val numeroPedido: String) :
            Screen(route = "detalle_pedido/{numeroPedido}") {
        companion object {
            const val routePattern = "detalle_pedido/{numeroPedido}"
            fun createRoute(numeroPedido: String) = "detalle_pedido/$numeroPedido"
        }
    }

    // Ejemplo antiguo (mantener por compatibilidad)
    data class Detail(val itemId: String) : Screen(route = "detail_page/{itemId}") {
        fun buildRoute(): String = route.replace("{itemId}", itemId)
    }
}
