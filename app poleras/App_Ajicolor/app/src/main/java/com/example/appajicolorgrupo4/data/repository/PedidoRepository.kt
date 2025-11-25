package com.example.appajicolorgrupo4.data.repository

import androidx.compose.ui.graphics.Color
import com.example.appajicolorgrupo4.data.*
import com.example.appajicolorgrupo4.data.local.pedido.PedidoDao
import com.example.appajicolorgrupo4.data.local.pedido.PedidoEntity
import com.example.appajicolorgrupo4.data.local.pedido.PedidoItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repositorio para gestionar pedidos en SQLite
 */
class PedidoRepository(private val pedidoDao: PedidoDao) {

    /**
     * Guarda un pedido completo en la base de datos
     */
    suspend fun guardarPedido(pedido: PedidoCompleto, userId: Long): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val pedidoEntity = PedidoEntity(
                    numeroPedido = pedido.numeroPedido,
                    nombreUsuario = pedido.nombreUsuario,
                    userId = userId,
                    subtotal = pedido.subtotal.toInt(),
                    impuestos = pedido.impuestos.toInt(),
                    costoEnvio = pedido.costoEnvio.toInt(),
                    total = pedido.total.toInt(),
                    direccionEnvio = pedido.direccionEnvio,
                    telefono = pedido.telefono,
                    notasAdicionales = pedido.notasAdicionales,
                    numeroDespacho = pedido.numeroDespacho,
                    metodoPago = pedido.metodoPago.name,
                    estado = pedido.estado.name,
                    fechaCreacion = pedido.fechaCreacion,
                    fechaConfirmacion = pedido.fechaConfirmacion,
                    fechaEnvio = pedido.fechaEnvio,
                    fechaEntrega = pedido.fechaEntrega
                )

                val items = pedido.productos.map { producto ->
                    PedidoItemEntity(
                        numeroPedido = pedido.numeroPedido,
                        productoId = producto.id,
                        productoNombre = producto.nombre,
                        productoImagenResId = producto.imagenResId ?: 0,
                        precio = producto.precio,
                        cantidad = producto.cantidad,
                        talla = producto.talla?.displayName ?: "N/A",
                        color = producto.color.hexCode,
                        colorNombre = producto.color.nombre,
                        categoria = producto.categoria.name
                    )
                }

                pedidoDao.insertPedido(pedidoEntity)
                pedidoDao.insertPedidoItems(items)

                Result.success(pedido.numeroPedido)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun obtenerPedidosUsuario(userId: Long): Flow<List<PedidoCompleto>> {
        return pedidoDao.getPedidosByUserId(userId).map { pedidos ->
            pedidos.map { pedidoEntity ->
                convertirAPedidoCompleto(pedidoEntity)
            }
        }
    }

    suspend fun obtenerPedidoPorNumero(numeroPedido: String): PedidoCompleto? {
        return withContext(Dispatchers.IO) {
            val pedidoEntity = pedidoDao.getPedidoByNumero(numeroPedido)
            if (pedidoEntity != null) {
                convertirAPedidoCompleto(pedidoEntity)
            } else {
                null
            }
        }
    }

    suspend fun deleteAllPedidos() {
        withContext(Dispatchers.IO) {
            pedidoDao.deleteAllPedidoItems()
            pedidoDao.deleteAllPedidos()
        }
    }

    suspend fun actualizarEstadoPedido(numeroPedido: String, nuevoEstado: EstadoPedido) {
        withContext(Dispatchers.IO) {
            pedidoDao.actualizarEstadoPedido(numeroPedido, nuevoEstado.name, System.currentTimeMillis())
        }
    }

    suspend fun asignarNumeroDespacho(numeroPedido: String, numeroDespacho: String) {
        withContext(Dispatchers.IO) {
            pedidoDao.asignarNumeroDespacho(numeroPedido, numeroDespacho, System.currentTimeMillis())
        }
    }

    private suspend fun convertirAPedidoCompleto(pedidoEntity: PedidoEntity): PedidoCompleto {
        val items = pedidoDao.getPedidoItems(pedidoEntity.numeroPedido)

        val productos = items.map { item ->
            val talla = Talla.tallasAdulto().find { it.valor == item.talla } ?: Talla.tallasInfantil().find { it.valor == item.talla }
            ProductoCarrito(
                id = item.productoId,
                nombre = item.productoNombre,
                precio = item.precio,
                cantidad = item.cantidad,
                color = ColorInfo(
                    nombre = item.colorNombre,
                    color = Color(android.graphics.Color.parseColor(item.color)),
                    hexCode = item.color
                ),
                categoria = CategoriaProducto.valueOf(item.categoria),
                imagenResId = item.productoImagenResId,
                talla = talla
            )
        }

        return PedidoCompleto(
            numeroPedido = pedidoEntity.numeroPedido,
            nombreUsuario = pedidoEntity.nombreUsuario,
            productos = productos,
            subtotal = pedidoEntity.subtotal.toDouble(),
            impuestos = pedidoEntity.impuestos.toDouble(),
            costoEnvio = pedidoEntity.costoEnvio.toDouble(),
            total = pedidoEntity.total.toDouble(),
            direccionEnvio = pedidoEntity.direccionEnvio,
            telefono = pedidoEntity.telefono,
            notasAdicionales = pedidoEntity.notasAdicionales,
            numeroDespacho = pedidoEntity.numeroDespacho,
            metodoPago = MetodoPago.valueOf(pedidoEntity.metodoPago),
            estado = EstadoPedido.valueOf(pedidoEntity.estado),
            fechaCreacion = pedidoEntity.fechaCreacion,
            fechaConfirmacion = pedidoEntity.fechaConfirmacion,
            fechaEnvio = pedidoEntity.fechaEnvio,
            fechaEntrega = pedidoEntity.fechaEntrega
        )
    }
}
