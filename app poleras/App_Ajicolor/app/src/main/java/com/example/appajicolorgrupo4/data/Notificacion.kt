package com.example.appajicolorgrupo4.data

import java.util.UUID

/**
 * Tipos de notificaciones disponibles
 */
enum class TipoNotificacion {
    COMPRA_EXITOSA,
    PEDIDO_CONFIRMADO,
    PEDIDO_ENVIADO,
    PEDIDO_ENTREGADO,
    PROMOCION,
    GENERAL
}

/**
 * Modelo de datos para notificaciones
 */
data class Notificacion(
    val id: String = UUID.randomUUID().toString(),
    val tipo: TipoNotificacion,
    val titulo: String,
    val mensaje: String,
    val numeroPedido: String? = null, // Para notificaciones de pedidos
    val timestamp: Long = System.currentTimeMillis(),
    val leida: Boolean = false,
    val accion: AccionNotificacion? = null
)

/**
 * Acción que se ejecuta al hacer clic en la notificación
 */
sealed class AccionNotificacion {
    data class VerPedido(val numeroPedido: String) : AccionNotificacion()
    data class Navegar(val ruta: String) : AccionNotificacion()
    data object Ninguna : AccionNotificacion()
}

