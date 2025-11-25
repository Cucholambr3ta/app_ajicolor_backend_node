package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.*
import com.example.appajicolorgrupo4.data.models.Product
import com.example.appajicolorgrupo4.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteProductoRepository(private val apiService: ApiService) {

    suspend fun obtenerProductos(): Result<List<Producto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProductos()
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    val domainProducts = products.map { it.toDomain() }
                    Result.success(domainProducts)
                } else {
                    Result.failure(Exception("Error fetching products: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun crearProducto(producto: Producto): Result<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                val productDto = producto.toDto()
                val response = apiService.createProduct(productDto)
                if (response.isSuccessful) {
                    val createdProduct = response.body()?.toDomain() ?: producto
                    Result.success(createdProduct)
                } else {
                    Result.failure(Exception("Error creating product: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun actualizarProducto(producto: Producto): Result<Producto> {
        return withContext(Dispatchers.IO) {
            try {
                val productDto = producto.toDto()
                val response = apiService.updateProduct(producto.id, productDto)
                if (response.isSuccessful) {
                    val updatedProduct = response.body()?.toDomain() ?: producto
                    Result.success(updatedProduct)
                } else {
                    Result.failure(Exception("Error updating product: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun eliminarProducto(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteProduct(id)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error deleting product: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Mappers
    private fun Product.toDomain(): Producto {
        return Producto(
            id = this.id,
            nombre = this.nombre,
            descripcion = this.descripcion,
            precio = this.precio,
            categoria = try { CategoriaProducto.valueOf(this.categoria) } catch(e: Exception) { CategoriaProducto.SERIGRAFIA },
            imagenResId = this.imagenResId ?: 0, // Placeholder
            imagenUrl = this.imagenUrl,
            stock = this.stock,
            calificacionPromedio = this.rating,
            numeroResenas = this.cantidadReviews,
            coloresDisponibles = this.coloresDisponibles?.map { ColorInfo("Color", androidx.compose.ui.graphics.Color.Gray, it) } ?: emptyList(),
            tallasDisponibles = if (this.tipoTalla == "ADULTO") Talla.tallasAdulto() else if (this.tipoTalla == "INFANTIL") Talla.tallasInfantil() else emptyList()
        )
    }

    private fun Producto.toDto(): Product {
        return Product(
            id = this.id,
            nombre = this.nombre,
            descripcion = this.descripcion,
            precio = this.precio,
            categoria = this.categoria.name,
            stock = this.stock,
            imagenResId = this.imagenResId,
            imagenUrl = this.imagenUrl,
            tipoTalla = if (this.tallasDisponibles.contains(Talla.S)) "ADULTO" else "INFANTIL",
            coloresDisponibles = this.coloresDisponibles.map { it.hexCode },
            rating = this.calificacionPromedio,
            cantidadReviews = this.numeroResenas
        )
    }
}
