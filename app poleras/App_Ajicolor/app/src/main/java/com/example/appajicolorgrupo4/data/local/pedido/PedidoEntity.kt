package com.example.appajicolorgrupo4.data.local.pedido

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room para almacenar pedidos en SQLite
 */
@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey
    val numeroPedido: String, // Ej: "ALE00001"

    val nombreUsuario: String,
    val userId: Long, // ID del usuario que hizo el pedido

    // Montos
    val subtotal: Int,
    val impuestos: Int,
    val costoEnvio: Int,
    val total: Int,

    // Información de entrega
    val direccionEnvio: String,
    val telefono: String,
    val notasAdicionales: String = "",
    val numeroDespacho: String? = null,

    // Método de pago
    val metodoPago: String, // Nombre del método de pago

    // Estado y fechas
    val estado: String, // Nombre del estado
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaConfirmacion: Long? = null,
    val fechaEnvio: Long? = null,
    val fechaEntrega: Long? = null
)

/**
 * Entidad de Room para almacenar items de pedidos
 */
@Entity(
    tableName = "pedido_items",
    primaryKeys = ["numeroPedido", "productoId", "talla", "color"]
)
data class PedidoItemEntity(
    val numeroPedido: String,
    val productoId: String,
    val productoNombre: String,
    val productoImagenResId: Int,
    val precio: Int,
    val cantidad: Int,
    val talla: String = "N/A", // Para accesorios sin talla usar "N/A"
    val color: String, // Código HEX del color
    val colorNombre: String,
    val categoria: String
)

