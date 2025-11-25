package com.example.appajicolorgrupo4.data.repository

import androidx.compose.ui.graphics.Color
import com.example.appajicolorgrupo4.data.*
import com.example.appajicolorgrupo4.data.models.Order
import com.example.appajicolorgrupo4.data.models.OrderProduct
import com.example.appajicolorgrupo4.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/** Repositorio para gestionar pedidos en el Backend */
class RemotePedidoRepository(private val apiService: ApiService) {

    /** Guarda un pedido en el backend */
    suspend fun guardarPedido(pedido: PedidoCompleto, userId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val orderProducts =
                        pedido.productos.map { producto ->
                            OrderProduct(
                                    producto = producto.id, // Asumiendo que ID es String
                                    cantidad = producto.cantidad,
                                    precioUnitario = producto.precio,
                                    talla = producto.talla?.valor,
                                    color = producto.color.hexCode
                            )
                        }

                val order =
                        Order(
                                numeroPedido = pedido.numeroPedido,
                                usuario = userId,
                                productos = orderProducts,
                                subtotal = pedido.subtotal.toInt(),
                                impuestos = pedido.impuestos.toInt(),
                                costoEnvio = pedido.costoEnvio.toInt(),
                                total = pedido.total.toInt(),
                                direccionEnvio = pedido.direccionEnvio,
                                telefono = pedido.telefono,
                                metodoPago = pedido.metodoPago.name,
                                estado = pedido.estado.name
                        )

                val response = apiService.createOrder(order)
                if (response.isSuccessful) {
                    Result.success(pedido.numeroPedido)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Result.failure(
                            Exception("Error al crear pedido: ${response.code()} - $errorBody")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /** Obtiene los pedidos del usuario desde el backend */
    fun obtenerPedidosUsuario(userId: String): Flow<List<PedidoCompleto>> {
        return flow<List<PedidoCompleto>> {
                    try {
                        val response = apiService.getOrdersByUser(userId)
                        if (response.isSuccessful) {
                            val orders: List<Order> = response.body() ?: emptyList()
                            val pedidosCompletos =
                                    orders.map { order: Order -> convertirAPedidoCompleto(order) }
                            emit(pedidosCompletos)
                        } else {
                            emit(emptyList<PedidoCompleto>()) // O manejar error
                        }
                    } catch (e: Exception) {
                        emit(emptyList<PedidoCompleto>())
                    }
                }
                .flowOn(Dispatchers.IO)
    }

    private fun convertirAPedidoCompleto(order: Order): PedidoCompleto {
        // Nota: El backend no devuelve todos los detalles del producto (nombre, imagen, etc.)
        // en la lista de pedidos, solo IDs. Idealmente el backend debería hacer 'populate'.
        // Por simplicidad, aquí mapeamos lo que tenemos.

        val productos =
                order.productos.map { item ->
                    ProductoCarrito(
                            id = item.producto,
                            nombre = "Producto ${item.producto}", // Placeholder si no hay populate
                            precio = item.precioUnitario,
                            cantidad = item.cantidad,
                            color = ColorInfo("Color", Color.Gray, item.color ?: "#000000"),
                            categoria = CategoriaProducto.SERIGRAFIA, // Default
                            imagenResId = null,
                            talla = Talla.tallasAdulto().find { it.valor == item.talla }
                    )
                }

        return PedidoCompleto(
                numeroPedido = order.numeroPedido,
                nombreUsuario = "Usuario", // No viene en Order
                productos = productos,
                subtotal = order.subtotal.toDouble(),
                impuestos = order.impuestos.toDouble(),
                costoEnvio = order.costoEnvio.toDouble(),
                total = order.total.toDouble(),
                direccionEnvio = order.direccionEnvio,
                telefono = order.telefono,
                metodoPago =
                        try {
                            MetodoPago.valueOf(order.metodoPago)
                        } catch (e: Exception) {
                            MetodoPago.EFECTIVO
                        },
                estado =
                        try {
                            EstadoPedido.valueOf(order.estado)
                        } catch (e: Exception) {
                            EstadoPedido.CONFIRMADO
                        },
                fechaCreacion =
                        System.currentTimeMillis() // Parsear fecha del backend si es necesario
        )
    }

    suspend fun obtenerPedidoPorNumero(numeroPedido: String): PedidoCompleto? {
        // Implementar endpoint GET /api/v1/pedidos/{numeroPedido} si existe
        return null
    }

    suspend fun actualizarEstadoPedido(numeroPedido: String, nuevoEstado: EstadoPedido) {
        // Implementar endpoint PUT /api/v1/pedidos/{numeroPedido}/estado
    }

    suspend fun asignarNumeroDespacho(numeroPedido: String, numeroDespacho: String) {
        // Implementar endpoint PUT /api/v1/pedidos/{numeroPedido}/despacho
    }
}
