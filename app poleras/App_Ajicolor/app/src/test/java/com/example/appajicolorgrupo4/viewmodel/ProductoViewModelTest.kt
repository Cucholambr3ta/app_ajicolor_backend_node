package com.example.appajicolorgrupo4.viewmodel

import com.example.appajicolorgrupo4.data.CategoriaProducto
import com.example.appajicolorgrupo4.data.Producto
import com.example.appajicolorgrupo4.data.repository.ProductoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    private lateinit var viewModel: ProductoViewModel
    private lateinit var repository: FakeProductoRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeProductoRepository()
        viewModel = ProductoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarProductosRemotos updates state with products on success`() = runTest(testDispatcher) {
        // Given
        val mockProducts = listOf(
            Producto(
                id = "1",
                nombre = "Test Product",
                descripcion = "Desc",
                precio = 1000,
                categoria = CategoriaProducto.SERIGRAFIA,
                imagenResId = 0,
                imagenUrl = "url",
                stock = 10,
                calificacionPromedio = 4.5f,
                numeroResenas = 5,
                coloresDisponibles = emptyList(),
                tallasDisponibles = emptyList()
            )
        )
        repository.productosResult = Result.success(mockProducts)

        // When
        viewModel.cargarProductosRemotos()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(mockProducts, viewModel.productos.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(null, viewModel.error.value)
    }

    @Test
    fun `cargarProductosRemotos updates error state on failure`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Network Error"
        repository.productosResult = Result.failure(Exception(errorMessage))

        // When
        viewModel.cargarProductosRemotos()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(emptyList<Producto>(), viewModel.productos.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(errorMessage, viewModel.error.value)
    }
}
