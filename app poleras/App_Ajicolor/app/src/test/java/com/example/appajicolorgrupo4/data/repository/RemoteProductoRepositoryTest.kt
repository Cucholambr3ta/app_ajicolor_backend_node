package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.CategoriaProducto
import com.example.appajicolorgrupo4.data.Producto
import com.example.appajicolorgrupo4.data.remote.ApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RemoteProductoRepositoryTest {

    private lateinit var repository: RemoteProductoRepository
    private val apiService = mockk<ApiService>()

    @Before
    fun setup() {
        repository = RemoteProductoRepository(apiService)
    }

    @Test
    fun `obtenerProductos returns success when api call is successful`() = runTest {
        // Given
        val mockProductDto = com.example.appajicolorgrupo4.data.models.Product(
            id = "1",
            nombre = "Test Product",
            descripcion = "Desc",
            precio = 1000,
            categoria = "SERIGRAFIA",
            stock = 10,
            imagenResId = 0,
            imagenUrl = "url",
            tipoTalla = "ADULTO",
            permiteTipoInfantil = false,
            coloresDisponibles = listOf("#FF0000"),
            rating = 4.5f,
            cantidadReviews = 5
        )
        coEvery { apiService.getProductos() } returns Response.success(listOf(mockProductDto))

        // When
        val result = repository.obtenerProductos()

        // Then
        assertTrue(result.isSuccess)
        val productos = result.getOrNull()
        assertTrue(productos != null && productos.isNotEmpty())
        assertEquals("1", productos!![0].id)
        assertEquals("Test Product", productos[0].nombre)
    }

    @Test
    fun `obtenerProductos returns failure when api call fails`() = runTest {
        // Given
        coEvery { apiService.getProductos() } returns Response.error(500, okhttp3.ResponseBody.create(null, "Error"))

        // When
        val result = repository.obtenerProductos()

        // Then
        assertTrue(result.isFailure)
    }
}
