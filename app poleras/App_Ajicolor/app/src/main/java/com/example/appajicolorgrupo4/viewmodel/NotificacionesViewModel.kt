package com.example.appajicolorgrupo4.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appajicolorgrupo4.data.AccionNotificacion
import com.example.appajicolorgrupo4.data.Notificacion
import com.example.appajicolorgrupo4.data.TipoNotificacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel para gestionar las notificaciones de la aplicación
 */
class NotificacionesViewModel : ViewModel() {

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    /**
     * Crea una notificación de compra exitosa
     */
    fun crearNotificacionCompraExitosa(numeroPedido: String) {
        val notificacion = Notificacion(
            tipo = TipoNotificacion.COMPRA_EXITOSA,
            titulo = "¡Compra exitosa!",
            mensaje = "Su compra con el número $numeroPedido ha sido exitosa",
            numeroPedido = numeroPedido,
            accion = AccionNotificacion.VerPedido(numeroPedido)
        )
        agregarNotificacion(notificacion)
    }

    /**
     * Crea una notificación de pedido confirmado
     */
    fun crearNotificacionPedidoConfirmado(numeroPedido: String) {
        val notificacion = Notificacion(
            tipo = TipoNotificacion.PEDIDO_CONFIRMADO,
            titulo = "Pedido confirmado",
            mensaje = "Tu pedido $numeroPedido ha sido confirmado y está siendo preparado",
            numeroPedido = numeroPedido,
            accion = AccionNotificacion.VerPedido(numeroPedido)
        )
        agregarNotificacion(notificacion)
    }

    /**
     * Crea una notificación de pedido enviado
     */
    fun crearNotificacionPedidoEnviado(numeroPedido: String) {
        val notificacion = Notificacion(
            tipo = TipoNotificacion.PEDIDO_ENVIADO,
            titulo = "Pedido en camino",
            mensaje = "Tu pedido $numeroPedido está en camino. ¡Pronto lo recibirás!",
            numeroPedido = numeroPedido,
            accion = AccionNotificacion.VerPedido(numeroPedido)
        )
        agregarNotificacion(notificacion)
    }

    /**
     * Crea una notificación de pedido entregado
     */
    fun crearNotificacionPedidoEntregado(numeroPedido: String) {
        val notificacion = Notificacion(
            tipo = TipoNotificacion.PEDIDO_ENTREGADO,
            titulo = "Pedido entregado",
            mensaje = "Tu pedido $numeroPedido ha sido entregado. ¡Disfrútalo!",
            numeroPedido = numeroPedido,
            accion = AccionNotificacion.VerPedido(numeroPedido)
        )
        agregarNotificacion(notificacion)
    }

    /**
     * Agrega una notificación a la lista
     */
    private fun agregarNotificacion(notificacion: Notificacion) {
        val notificacionesActuales = _notificaciones.value.toMutableList()
        notificacionesActuales.add(0, notificacion) // Agregar al inicio (más reciente primero)
        _notificaciones.value = notificacionesActuales
    }

    /**
     * Marca una notificación como leída
     */
    fun marcarComoLeida(notificacionId: String) {
        _notificaciones.value = _notificaciones.value.map { notificacion ->
            if (notificacion.id == notificacionId) {
                notificacion.copy(leida = true)
            } else {
                notificacion
            }
        }
    }

    /**
     * Elimina una notificación
     */
    fun eliminarNotificacion(notificacionId: String) {
        _notificaciones.value = _notificaciones.value.filter { it.id != notificacionId }
    }

    /**
     * Elimina todas las notificaciones leídas
     */
    fun eliminarNotificacionesLeidas() {
        _notificaciones.value = _notificaciones.value.filter { !it.leida }
    }

    /**
     * Obtiene el número de notificaciones no leídas
     */
    fun obtenerNoLeidas(): Int {
        return _notificaciones.value.count { !it.leida }
    }

    /**
     * Marca todas las notificaciones como leídas
     */
    fun marcarTodasComoLeidas() {
        _notificaciones.value = _notificaciones.value.map { it.copy(leida = true) }
    }

    /**
     * Limpia todas las notificaciones
     */
    fun limpiarTodas() {
        _notificaciones.value = emptyList()
    }
}

