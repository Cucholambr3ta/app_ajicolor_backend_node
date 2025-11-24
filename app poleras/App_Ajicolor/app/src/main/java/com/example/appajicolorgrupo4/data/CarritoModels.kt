package com.example.appajicolorgrupo4.data

/**
 * Representa un producto en el carrito de compras
 */
data class ProductoCarrito(
    val id: String,
    val nombre: String,
    val precio: Int, // Cambiado a Int
    val cantidad: Int = 1,
    val talla: Talla?,
    val color: ColorInfo,
    val categoria: CategoriaProducto,
    val imagenResId: Int
) {
    /**
     * Calcula el subtotal de este item (precio * cantidad)
     */
    fun subtotal(): Int = precio * cantidad

    /**
     * Formatea el subtotal con símbolo $
     */
    fun subtotalFormateado(): String = "$${ subtotal() }"

    /**
     * Formatea el precio con símbolo $
     */
    fun precioFormateado(): String = "$$precio"
}

/**
 * Métodos de pago disponibles
 */
enum class MetodoPago(val displayName: String, val icono: String) {
    TARJETA_CREDITO("Tarjeta de Crédito", "💳"),
    TARJETA_DEBITO("Tarjeta de Débito", "💳"),
    YAPE("Yape", "📱"),
    PLIN("Plin", "📱"),
    TRANSFERENCIA("Transferencia Bancaria", "🏦"),
    EFECTIVO("Efectivo contra entrega", "💵")
}

/**
 * NOTA: EstadoPedido se define en PedidoCompleto.kt
 * Se eliminó de aquí para evitar duplicados
 */

/**
 * Representa un pedido completo
 * NOTA: Usar PedidoCompleto de PedidoCompleto.kt en su lugar
 */
data class Pedido(
    val id: String,
    val productos: List<ProductoCarrito>,
    val subtotal: Int,
    val costoEnvio: Int,
    val total: Int,
    val direccionEnvio: String,
    val metodoPago: MetodoPago,
    val estado: EstadoPedido = EstadoPedido.CONFIRMADO,
    val fechaPedido: Long = System.currentTimeMillis()
)

