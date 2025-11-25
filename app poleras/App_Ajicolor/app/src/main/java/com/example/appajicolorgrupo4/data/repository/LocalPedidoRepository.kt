package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.local.pedido.PedidoDao

class LocalPedidoRepository(private val pedidoDao: PedidoDao) {
    suspend fun deleteAllPedidos() {
        pedidoDao.deleteAllPedidos()
    }
}
