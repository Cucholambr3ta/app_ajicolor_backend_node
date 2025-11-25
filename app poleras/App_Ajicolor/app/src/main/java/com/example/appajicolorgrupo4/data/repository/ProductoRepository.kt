package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.Producto

interface ProductoRepository {
    suspend fun obtenerProductos(): Result<List<Producto>>
    suspend fun crearProducto(producto: Producto): Result<Producto>
    suspend fun actualizarProducto(producto: Producto): Result<Producto>
    suspend fun eliminarProducto(id: String): Result<Unit>
}
