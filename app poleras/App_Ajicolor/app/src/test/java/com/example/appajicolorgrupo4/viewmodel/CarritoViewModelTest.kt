package com.example.appajicolorgrupo4.viewmodel

import androidx.compose.ui.graphics.Color
import com.example.appajicolorgrupo4.data.CategoriaProducto
import com.example.appajicolorgrupo4.data.ColorInfo
import com.example.appajicolorgrupo4.data.ProductoCarrito
import com.example.appajicolorgrupo4.data.Talla
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModelTest {

    private lateinit var viewModel: CarritoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = CarritoViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `agregarProducto adds product to list`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.subtotal.collect()
        }

        // Given
        val producto = ProductoCarrito(
            id = "1",
            nombre = "Test Product",
            precio = 1000,
            cantidad = 1,
            talla = Talla.M,
            color = ColorInfo("Red", Color.Red, "#FF0000"),
            categoria = CategoriaProducto.SERIGRAFIA,
            imagenResId = 0
        )

        // When
        viewModel.agregarProducto(producto)

        // Then
        assertEquals(listOf(producto), viewModel.productos.value)
        assertEquals(1000, viewModel.subtotal.value)
    }

    @Test
    fun `agregarProducto increments quantity if product exists`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.subtotal.collect()
        }

        // Given
        val producto = ProductoCarrito(
            id = "1",
            nombre = "Test Product",
            precio = 1000,
            cantidad = 1,
            talla = Talla.M,
            color = ColorInfo("Red", Color.Red, "#FF0000"),
            categoria = CategoriaProducto.SERIGRAFIA,
            imagenResId = 0
        )
        viewModel.agregarProducto(producto)

        // When
        viewModel.agregarProducto(producto)

        // Then
        assertEquals(1, viewModel.productos.value.size)
        assertEquals(2, viewModel.productos.value[0].cantidad)
        assertEquals(2000, viewModel.subtotal.value)
    }

    @Test
    fun `eliminarProducto removes product from list`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.subtotal.collect()
        }

        // Given
        val producto = ProductoCarrito(
            id = "1",
            nombre = "Test Product",
            precio = 1000,
            cantidad = 1,
            talla = Talla.M,
            color = ColorInfo("Red", Color.Red, "#FF0000"),
            categoria = CategoriaProducto.SERIGRAFIA,
            imagenResId = 0
        )
        viewModel.agregarProducto(producto)

        // When
        viewModel.eliminarProducto(producto)

        // Then
        assertEquals(emptyList<ProductoCarrito>(), viewModel.productos.value)
        assertEquals(0, viewModel.subtotal.value)
    }

    @Test
    fun `actualizarCantidad updates quantity`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.subtotal.collect()
        }

        // Given
        val producto = ProductoCarrito(
            id = "1",
            nombre = "Test Product",
            precio = 1000,
            cantidad = 1,
            talla = Talla.M,
            color = ColorInfo("Red", Color.Red, "#FF0000"),
            categoria = CategoriaProducto.SERIGRAFIA,
            imagenResId = 0
        )
        viewModel.agregarProducto(producto)

        // When
        viewModel.actualizarCantidad(producto, 5)

        // Then
        assertEquals(5, viewModel.productos.value[0].cantidad)
        assertEquals(5000, viewModel.subtotal.value)
    }

    @Test
    fun `actualizarCantidad removes product if quantity is 0`() = runTest {
        // Given
        val producto = ProductoCarrito(
            id = "1",
            nombre = "Test Product",
            precio = 1000,
            cantidad = 1,
            talla = Talla.M,
            color = ColorInfo("Red", Color.Red, "#FF0000"),
            categoria = CategoriaProducto.SERIGRAFIA,
            imagenResId = 0
        )
        viewModel.agregarProducto(producto)

        // When
        viewModel.actualizarCantidad(producto, 0)

        // Then
        assertEquals(emptyList<ProductoCarrito>(), viewModel.productos.value)
    }

    @Test
    fun `limpiarCarrito removes all products`() = runTest {
        // Given
        val producto = ProductoCarrito(
            id = "1",
            nombre = "Test Product",
            precio = 1000,
            cantidad = 1,
            talla = Talla.M,
            color = ColorInfo("Red", Color.Red, "#FF0000"),
            categoria = CategoriaProducto.SERIGRAFIA,
            imagenResId = 0
        )
        viewModel.agregarProducto(producto)

        // When
        viewModel.limpiarCarrito()

        // Then
        assertEquals(emptyList<ProductoCarrito>(), viewModel.productos.value)
    }

    @Test
    fun `calculations are correct`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            launch { viewModel.subtotal.collect() }
            launch { viewModel.iva.collect() }
            launch { viewModel.costoEnvio.collect() }
            launch { viewModel.total.collect() }
            launch { viewModel.calificaEnvioGratis.collect() }
        }

        // Given
        val producto = ProductoCarrito(
            id = "1",
            nombre = "Test Product",
            precio = 10000,
            cantidad = 1,
            talla = Talla.M,
            color = ColorInfo("Red", Color.Red, "#FF0000"),
            categoria = CategoriaProducto.SERIGRAFIA,
            imagenResId = 0
        )
        viewModel.agregarProducto(producto)

        // Subtotal: 10000
        // IVA (19%): 1900
        // Costo Envio (Normal): 5000 (Subtotal < 20000)
        // Total: 16900

        // Then
        assertEquals(10000, viewModel.subtotal.value)
        assertEquals(1900, viewModel.iva.value)
        assertEquals(5000, viewModel.costoEnvio.value)
        assertEquals(16900, viewModel.total.value)
        assertEquals(false, viewModel.calificaEnvioGratis.value)

        // Add another product to reach free shipping
        viewModel.actualizarCantidad(producto, 2) // Subtotal 20000

        // Subtotal: 20000
        // IVA: 3800
        // Costo Envio: 0
        // Total: 23800

        assertEquals(20000, viewModel.subtotal.value)
        assertEquals(3800, viewModel.iva.value)
        assertEquals(0, viewModel.costoEnvio.value)
        assertEquals(23800, viewModel.total.value)
        assertEquals(true, viewModel.calificaEnvioGratis.value)
    }
}
