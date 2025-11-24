package com.example.appajicolorgrupo4.data.local.pedido

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO para gestionar los pedidos en la base de datos
 */
@Dao
interface PedidoDao {

    // ---- INSERCIONES ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedido(pedido: PedidoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedidoItems(items: List<PedidoItemEntity>)

    // ---- CONSULTAS ----
    @Query("SELECT * FROM pedidos WHERE userId = :userId ORDER BY fechaCreacion DESC")
    fun getPedidosByUserId(userId: Long): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE numeroPedido = :numeroPedido LIMIT 1")
    suspend fun getPedidoByNumero(numeroPedido: String): PedidoEntity?

    @Query("SELECT * FROM pedido_items WHERE numeroPedido = :numeroPedido")
    suspend fun getPedidoItems(numeroPedido: String): List<PedidoItemEntity>

    @Query("SELECT * FROM pedidos WHERE userId = :userId AND estado = :estado ORDER BY fechaCreacion DESC")
    fun getPedidosByEstado(userId: Long, estado: String): Flow<List<PedidoEntity>>

    @Query("SELECT COUNT(*) FROM pedidos")
    suspend fun getContadorPedidos(): Int

    // ---- ACTUALIZACIONES ----
    @Query("UPDATE pedidos SET estado = :nuevoEstado, fechaConfirmacion = :fecha WHERE numeroPedido = :numeroPedido AND estado = 'CONFIRMADO'")
    suspend fun actualizarEstadoPedido(numeroPedido: String, nuevoEstado: String, fecha: Long)

    @Query("UPDATE pedidos SET numeroDespacho = :numeroDespacho, fechaEnvio = :fecha WHERE numeroPedido = :numeroPedido")
    suspend fun asignarNumeroDespacho(numeroPedido: String, numeroDespacho: String, fecha: Long)

    // ---- ELIMINACIÓN ----
    @Query("DELETE FROM pedidos")
    suspend fun deleteAllPedidos()

    @Query("DELETE FROM pedido_items")
    suspend fun deleteAllPedidoItems()
}
