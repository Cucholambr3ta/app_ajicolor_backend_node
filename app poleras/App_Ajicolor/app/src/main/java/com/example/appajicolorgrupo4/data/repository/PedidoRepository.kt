package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.PedidoCompleto
import kotlinx.coroutines.flow.Flow

interface PedidoRepository {
    suspend fun guardarPedido(pedido: PedidoCompleto, userId: String): Result<String>
    fun obtenerPedidosUsuario(userId: String): Flow<List<PedidoCompleto>>
    fun obtenerTodosLosPedidos(): Flow<List<PedidoCompleto>>
    suspend fun obtenerPedidoPorNumero(numeroPedido: String): PedidoCompleto?
    suspend fun actualizarEstadoPedido(numeroPedido: String, nuevoEstado: EstadoPedido)
    suspend fun asignarNumeroDespacho(numeroPedido: String, numeroDespacho: String)
}
