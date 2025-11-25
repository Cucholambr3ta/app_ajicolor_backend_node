package com.example.appajicolorgrupo4.viewmodel

import com.example.appajicolorgrupo4.data.Producto
import com.example.appajicolorgrupo4.data.repository.ProductoRepository

class FakeProductoRepository : ProductoRepository {

    var productosResult: Result<List<Producto>> = Result.success(emptyList())
    var createResult: Result<Producto>? = null
    var updateResult: Result<Producto>? = null
    var deleteResult: Result<Unit>? = null

    override suspend fun obtenerProductos(): Result<List<Producto>> {
        return productosResult
    }

    override suspend fun crearProducto(producto: Producto): Result<Producto> {
        return createResult ?: Result.success(producto)
    }

    override suspend fun actualizarProducto(producto: Producto): Result<Producto> {
        return updateResult ?: Result.success(producto)
    }

    override suspend fun eliminarProducto(id: String): Result<Unit> {
        return deleteResult ?: Result.success(Unit)
    }
}
