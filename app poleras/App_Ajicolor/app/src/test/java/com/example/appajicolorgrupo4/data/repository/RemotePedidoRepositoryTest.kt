package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.CategoriaProducto
import com.example.appajicolorgrupo4.data.ColorInfo
import com.example.appajicolorgrupo4.data.EstadoPedido
import com.example.appajicolorgrupo4.data.MetodoPago
import com.example.appajicolorgrupo4.data.PedidoCompleto
import com.example.appajicolorgrupo4.data.ProductoCarrito
import com.example.appajicolorgrupo4.data.Talla
import com.example.appajicolorgrupo4.data.models.Order
import com.example.appajicolorgrupo4.data.models.OrderProduct
import com.example.appajicolorgrupo4.data.remote.ApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RemotePedidoRepositoryTest {

    private lateinit var repository: RemotePedidoRepository
    private val apiService = mockk<ApiService>()

    @Before
    fun setup() {
        repository = RemotePedidoRepository(apiService)
    }

    @Test
    fun `guardarPedido returns success when api call is successful`() = runTest {
        // Given
        val pedido = PedidoCompleto(
            numeroPedido = "123",
            nombreUsuario = "User",
            productos = listOf(
                ProductoCarrito(
                    id = "1",
                    nombre = "Product",
                    precio = 1000,
                    cantidad = 1,
                    talla = Talla.M,
                    color = ColorInfo("Red", androidx.compose.ui.graphics.Color.Red, "#FF0000"),
                    categoria = CategoriaProducto.SERIGRAFIA,
                    imagenResId = 0
                )
            ),
            subtotal = 1000.0,
            impuestos = 190.0,
            costoEnvio = 0.0,
            total = 1190.0,
            direccionEnvio = "Address",
            telefono = "123456",
            metodoPago = MetodoPago.EFECTIVO,
            estado = EstadoPedido.CONFIRMADO,
            fechaCreacion = System.currentTimeMillis()
        )
        
        coEvery { apiService.createOrder(any()) } returns Response.success(
            Order(
                id = "1",
                numeroPedido = "123",
                usuario = "1",
                productos = emptyList(),
                subtotal = 1000,
                impuestos = 190,
                costoEnvio = 0,
                total = 1190,
                direccionEnvio = "Address",
                telefono = "123456",
                metodoPago = "EFECTIVO",
                estado = "CONFIRMADO"
            )
        )

        // When
        val result = repository.guardarPedido(pedido, "1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("123", result.getOrNull())
    }

    @Test
    fun `obtenerPedidosUsuario returns list when api call is successful`() = runTest {
        // Given
        val mockOrder = Order(
            id = "1",
            numeroPedido = "123",
            usuario = "1",
            productos = listOf(
                OrderProduct(
                    producto = "1",
                    cantidad = 1,
                    precioUnitario = 1000,
                    talla = "M",
                    color = "#FF0000"
                )
            ),
            subtotal = 1000,
            impuestos = 190,
            costoEnvio = 0,
            total = 1190,
            direccionEnvio = "Address",
            telefono = "123456",
            metodoPago = "EFECTIVO",
            estado = "CONFIRMADO"
        )
        coEvery { apiService.getOrdersByUser("1") } returns Response.success(listOf(mockOrder))

        // When
        val result = repository.obtenerPedidosUsuario("1").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("123", result[0].numeroPedido)
    }
}
