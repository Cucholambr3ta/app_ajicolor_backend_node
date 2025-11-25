package com.example.appajicolorgrupo4.viewmodel

import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.PedidoCompleto
import com.example.appajicolorgrupo4.data.repository.PedidoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class FakePedidoRepository : PedidoRepository {

    var guardarResult: Result<String> = Result.success("12345")
    var pedidosUsuarioFlow: Flow<List<PedidoCompleto>> = flow { emit(emptyList()) }
    var todosLosPedidosFlow: Flow<List<PedidoCompleto>> = flow { emit(emptyList()) }
    var pedidoPorNumero: PedidoCompleto? = null

    override suspend fun guardarPedido(pedido: PedidoCompleto, userId: String): Result<String> {
        return guardarResult
    }

    override fun obtenerPedidosUsuario(userId: String): Flow<List<PedidoCompleto>> {
        return pedidosUsuarioFlow
    }

    override fun obtenerTodosLosPedidos(): Flow<List<PedidoCompleto>> {
        return todosLosPedidosFlow
    }

    override suspend fun obtenerPedidoPorNumero(numeroPedido: String): PedidoCompleto? {
        return pedidoPorNumero
    }

    override suspend fun actualizarEstadoPedido(numeroPedido: String, nuevoEstado: EstadoPedido) {
        // No-op
    }

    override suspend fun asignarNumeroDespacho(numeroPedido: String, numeroDespacho: String) {
        // No-op
    }
}
